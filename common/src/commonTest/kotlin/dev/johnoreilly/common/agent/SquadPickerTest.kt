package dev.johnoreilly.common.agent

import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SQUAD_TOTAL_SPEND = 1000 // £100.0m, tenths of a million
private const val CLUB_LIMIT = 3
private val POSITION_QUOTAS = mapOf(1 to 2, 2 to 5, 3 to 5, 4 to 3)

class SquadPickerTest {

    @Test
    fun `season-long pick respects squad size, position, budget and club constraints`() {
        val players = syntheticPlayerPool()

        val selection = pickSquad(
            players = players,
            fixtures = emptyList(),
            currentGameweek = 10,
            squadTotalSpend = SQUAD_TOTAL_SPEND,
            clubLimit = CLUB_LIMIT,
            lookaheadGameweeks = null,
        )

        assertSquadIsValid(selection)
    }

    @Test
    fun `fixture-run pick respects squad size, position, budget and club constraints`() {
        val players = syntheticPlayerPool()
        val fixtures = syntheticFixturePool()

        val selection = pickSquad(
            players = players,
            fixtures = fixtures,
            currentGameweek = 1,
            squadTotalSpend = SQUAD_TOTAL_SPEND,
            clubLimit = CLUB_LIMIT,
            lookaheadGameweeks = 5,
        )

        assertSquadIsValid(selection)
    }

    @Test
    fun `unavailable players are never selected`() {
        val players = syntheticPlayerPool().map {
            if (it.id == 1) it.copy(status = "i", chanceOfPlayingNextRound = 0) else it
        }

        val selection = pickSquad(
            players = players,
            fixtures = emptyList(),
            currentGameweek = 10,
            squadTotalSpend = SQUAD_TOTAL_SPEND,
            clubLimit = CLUB_LIMIT,
            lookaheadGameweeks = null,
        )

        assertTrue(selection.players.none { it.id == 1 })
        assertSquadIsValid(selection)
    }

    @Test
    fun `starting XI picks a valid formation, captain and vice-captain`() {
        val players = syntheticPlayerPool()
        val fixtures = syntheticFixturePool()
        val squad = pickSquad(
            players = players,
            fixtures = fixtures,
            currentGameweek = 1,
            squadTotalSpend = SQUAD_TOTAL_SPEND,
            clubLimit = CLUB_LIMIT,
            lookaheadGameweeks = null,
        ).players

        val xi = pickStartingXI(squad, fixtures, gameweek = 1)

        assertEquals(11, xi.startingXI.size)
        assertEquals(4, xi.bench.size)

        val countsByPosition = xi.startingXI.groupingBy { it.elementType }.eachCount()
        assertEquals(1, countsByPosition[1] ?: 0, "expected exactly one starting goalkeeper")
        val def = countsByPosition[2] ?: 0
        val mid = countsByPosition[3] ?: 0
        val fwd = countsByPosition[4] ?: 0
        assertTrue(def in 3..5, "defender count out of range: $def")
        assertTrue(mid in 2..5, "midfielder count out of range: $mid")
        assertTrue(fwd in 1..3, "forward count out of range: $fwd")
        assertEquals(10, def + mid + fwd)

        assertTrue(xi.captain in xi.startingXI)
        assertTrue(xi.viceCaptain in xi.startingXI)
        assertTrue(xi.captain.id != xi.viceCaptain.id)
    }

    private fun assertSquadIsValid(selection: SquadSelection) {
        assertEquals(15, selection.players.size)

        val countsByPosition = selection.players.groupingBy { it.elementType }.eachCount()
        POSITION_QUOTAS.forEach { (elementType, quota) ->
            assertEquals(quota, countsByPosition[elementType] ?: 0, "unexpected count for elementType=$elementType")
        }

        assertTrue(selection.totalCost <= SQUAD_TOTAL_SPEND / 10.0, "budget exceeded: ${selection.totalCost}")

        val countsByClub = selection.players.groupingBy { it.teamId }.eachCount()
        countsByClub.values.forEach { count ->
            assertTrue(count <= CLUB_LIMIT, "club limit exceeded: $count")
        }
    }
}

// 6 clubs, several candidates per position per club so quotas can be met without breaching the
// per-club limit (15 players / 3-per-club needs at least 5 clubs).
private fun syntheticPlayerPool(): List<Player> {
    var nextId = 1
    val players = mutableListOf<Player>()

    fun addPlayers(elementType: Int, count: Int, priceRange: ClosedFloatingPointRange<Double>) {
        repeat(count) { index ->
            val id = nextId++
            val teamId = (index % 6) + 1
            val price = priceRange.start + (priceRange.endInclusive - priceRange.start) * (index % 5) / 5.0
            val points = 20 + index * 3
            players += Player(
                id = id,
                name = "Player$id",
                team = "Team$teamId",
                photoUrl = "",
                points = points,
                currentPrice = price,
                goalsScored = 0,
                assists = 0,
                elementType = elementType,
                teamId = teamId,
                status = "a",
                chanceOfPlayingNextRound = 100,
            )
        }
    }

    // Kept low enough that even the priciest possible combination (2 GK + 5 DEF + 5 MID + 3 FWD,
    // each at the top of its range) stays comfortably under the £100.0m budget, so the greedy
    // picker's lack of budget lookahead across positions can never starve a later position.
    addPlayers(elementType = 1, count = 6, priceRange = 3.5..4.5)
    addPlayers(elementType = 2, count = 15, priceRange = 3.5..4.5)
    addPlayers(elementType = 3, count = 15, priceRange = 4.0..6.0)
    addPlayers(elementType = 4, count = 9, priceRange = 4.0..6.0)

    return players
}

private fun syntheticFixturePool(): List<GameFixture> {
    var nextId = 1
    val fixtures = mutableListOf<GameFixture>()
    val pairings = listOf(1 to 2, 3 to 4, 5 to 6)

    for (gameweek in 1..8) {
        pairings.forEach { (home, away) ->
            fixtures += GameFixture(
                id = nextId++,
                localKickoffTime = null,
                homeTeam = "Team$home",
                awayTeam = "Team$away",
                homeTeamPhotoUrl = "",
                awayTeamPhotoUrl = "",
                homeTeamScore = null,
                awayTeamScore = null,
                event = gameweek,
                homeTeamId = home,
                awayTeamId = away,
                homeDifficulty = if (home <= 3) 2 else 4,
                awayDifficulty = if (away <= 3) 2 else 4,
            )
        }
    }
    return fixtures
}
