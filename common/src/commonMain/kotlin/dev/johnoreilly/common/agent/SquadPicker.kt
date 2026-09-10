package dev.johnoreilly.common.agent

import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.Player

// FPL's element_type convention: 1=GK, 2=DEF, 3=MID, 4=FWD. Mirrors ElementTypeDto.squad_select,
// which hasn't changed in FPL's history.
private val POSITION_QUOTAS = mapOf(1 to 2, 2 to 5, 3 to 5, 4 to 3)

data class SquadSelection(
    val players: List<Player>,
    val totalCost: Double,
    val totalScore: Double,
)

data class StartingXISelection(
    val startingXI: List<Player>,
    val bench: List<Player>,
    val captain: Player,
    val viceCaptain: Player,
)

// Every DEF/MID/FWD split FPL allows: 3-5 DEF, 2-5 MID, 1-3 FWD, 10 outfield players (+1 GK).
private val VALID_FORMATIONS: List<Triple<Int, Int, Int>> = buildList {
    for (def in 3..5) for (mid in 2..5) for (fwd in 1..3) {
        if (def + mid + fwd == 10) add(Triple(def, mid, fwd))
    }
}

/**
 * Greedy-fill + bounded local-search squad selection. Not provably optimal (that would need an
 * ILP solver), but respects budget/position/club-limit constraints and runs on every KMP target.
 */
fun pickSquad(
    players: List<Player>,
    fixtures: List<GameFixture>,
    currentGameweek: Int,
    squadTotalSpend: Int,
    clubLimit: Int,
    lookaheadGameweeks: Int? = null,
): SquadSelection {
    val eligible = players.filter { it.status == "a" && (it.chanceOfPlayingNextRound ?: 100) >= 75 }
    val budget = squadTotalSpend / 10.0

    // Season-long mode ranks by points alone. Lookahead mode always blends both signals - total
    // points plus fixture ease - added rather than multiplied, so a strong fixture run can still
    // lift a player early in the season when points are near zero, without points ever being
    // zeroed out or ignored once they start accumulating.
    val score: (Player) -> Double = if (lookaheadGameweeks == null) {
        { it.points.toDouble() }
    } else {
        val easeByTeam = eligible.map { it.teamId }.distinct().associateWith { teamId ->
            fixtureEase(teamId, fixtures, currentGameweek, lookaheadGameweeks)
        }
        val fixtureRunScore: (Player) -> Double =
            { player -> player.points.toDouble() + (easeByTeam[player.teamId] ?: 0.0) }
        fixtureRunScore
    }

    val selected = mutableListOf<Player>()
    val clubCounts = mutableMapOf<Int, Int>()
    var totalCost = 0.0

    fun add(player: Player) {
        selected.add(player)
        clubCounts[player.teamId] = (clubCounts[player.teamId] ?: 0) + 1
        totalCost += player.currentPrice
    }

    fun remove(player: Player) {
        selected.remove(player)
        clubCounts[player.teamId] = (clubCounts[player.teamId] ?: 1) - 1
        totalCost -= player.currentPrice
    }

    POSITION_QUOTAS.forEach { (elementType, quota) ->
        val candidates = eligible
            .filter { it.elementType == elementType }
            .sortedByDescending { pointsPerCost(score(it), it.currentPrice) }

        for (candidate in candidates) {
            if (selected.count { it.elementType == elementType } >= quota) break
            val clubCount = clubCounts[candidate.teamId] ?: 0
            if (totalCost + candidate.currentPrice <= budget && clubCount < clubLimit) {
                add(candidate)
            }
        }
    }

    var pass = 0
    while (pass < 3) {
        var improved = false
        for (elementType in POSITION_QUOTAS.keys) {
            val positionCandidatesByScore = eligible
                .filter { it.elementType == elementType }
                .sortedByDescending { score(it) }

            for (current in selected.filter { it.elementType == elementType }) {
                val betterReplacement = positionCandidatesByScore.firstOrNull { candidate ->
                    candidate !in selected &&
                        score(candidate) > score(current) &&
                        (totalCost - current.currentPrice + candidate.currentPrice) <= budget &&
                        ((clubCounts[candidate.teamId] ?: 0) -
                            (if (candidate.teamId == current.teamId) 1 else 0)) < clubLimit
                }
                if (betterReplacement != null) {
                    remove(current)
                    add(betterReplacement)
                    improved = true
                }
            }
        }
        if (!improved) break
        pass++
    }

    return SquadSelection(selected, totalCost, selected.sumOf { score(it) })
}

/**
 * Picks the best-scoring valid starting XI (and captain/vice-captain) out of a 15-player squad for
 * a single gameweek. A small enough search space (15 players, 8 valid formations) to just try every
 * formation and keep the best, rather than needing the squad picker's greedy/local-search machinery.
 */
fun pickStartingXI(squad: List<Player>, fixtures: List<GameFixture>, gameweek: Int): StartingXISelection {
    val scoreFor = squad.associateWith { player ->
        player.points.toDouble() + fixtureEase(player.teamId, fixtures, gameweek, weeks = 1)
    }

    val byPosition = squad.groupBy { it.elementType }
    fun topByScore(elementType: Int, count: Int): List<Player> =
        (byPosition[elementType] ?: emptyList())
            .sortedByDescending { scoreFor.getValue(it) }
            .take(count)

    val goalkeeper = topByScore(1, 1)

    var bestXI: List<Player>? = null
    var bestScore = Double.NEGATIVE_INFINITY
    VALID_FORMATIONS.forEach { (defCount, midCount, fwdCount) ->
        val defenders = topByScore(2, defCount)
        val midfielders = topByScore(3, midCount)
        val forwards = topByScore(4, fwdCount)
        if (defenders.size == defCount && midfielders.size == midCount && forwards.size == fwdCount) {
            val xi = goalkeeper + defenders + midfielders + forwards
            val totalScore = xi.sumOf { scoreFor.getValue(it) }
            if (totalScore > bestScore) {
                bestScore = totalScore
                bestXI = xi
            }
        }
    }

    val startingXI = bestXI ?: squad.sortedByDescending { scoreFor.getValue(it) }.take(11)
    val bench = squad - startingXI.toSet()
    val captainOrder = startingXI.sortedByDescending { scoreFor.getValue(it) }

    return StartingXISelection(
        startingXI = startingXI,
        bench = bench,
        captain = captainOrder[0],
        viceCaptain = captainOrder[1],
    )
}

// Sums (6 - difficulty) across every fixture in the window; a team with two fixtures in one
// gameweek is counted twice, a blank gameweek contributes zero — no special-casing needed.
private fun fixtureEase(teamId: Int, fixtures: List<GameFixture>, fromGameweek: Int, weeks: Int): Double {
    val range = fromGameweek until (fromGameweek + weeks)
    return fixtures
        .filter { it.event in range && (it.homeTeamId == teamId || it.awayTeamId == teamId) }
        .sumOf { fixture ->
            val difficulty = if (fixture.homeTeamId == teamId) fixture.homeDifficulty else fixture.awayDifficulty
            (6 - difficulty).toDouble()
        }
}

private fun pointsPerCost(score: Double, price: Double): Double =
    if (price <= 0.0) score else score / price
