package dev.johnoreilly.common.agent

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.format.playersAsCsv
import dev.johnoreilly.common.model.Player
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable


class GetPlayersTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Unit>(
    argsType = typeToken<Unit>(),
    name = "getPlayers",
    description = "Get every player with the stats needed to compare them: price, total points, " +
        "recent form, minutes played, points per game, ownership, expected points for the next " +
        "gameweek, expected goal involvements per 90, bonus, clean sheets, set-piece duty, " +
        "availability status and injury news. Returns compact CSV with a legend."
) {
    private var playersCsv: String? = null

    override suspend fun execute(args: Unit): String {
        try {
            // Shared with the MCP server's get-players so both surfaces feed the model the same
            // compact rows; a data-class toString of the pool costs roughly four times the tokens
            // and crowds out the model's own reasoning.
            return playersCsv ?: fantasyPremierLeagueRepository.playersAsCsv().also { playersCsv = it }
        } catch (e: Exception) {
            println("Error: $e")
            return ""
        }
    }

}


class GetFixturesTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Unit>(
    argsType = typeToken<Unit>(),
    name = "getFixtures",
    description = "Get the list of fixtures"
) {
    override suspend fun execute(args: Unit): String {
        try {
            val fixtures = fantasyPremierLeagueRepository.getFixtures().first()
            return fixtures.toString()
        } catch (e: Exception) {
            println("Error: $e")
            return ""
        }
    }

}


class GetLeagueStandingsTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Unit>(
    argsType = typeToken<Unit>(),
    name = "getLeagueStandings",
    description = "Get the standings for the user's tracked mini-leagues"
) {
    override suspend fun execute(args: Unit): String {
        try {
            val leagueIds = fantasyPremierLeagueRepository.leagues.first()
            if (leagueIds.isEmpty()) {
                return "The user is not tracking any mini-leagues."
            }
            return leagueIds.mapNotNull { leagueId ->
                runCatching {
                    val standings = fantasyPremierLeagueRepository.getLeagueStandings(leagueId.trim().toInt())
                    val rows = standings.standings.results.joinToString("\n") { result ->
                        "${result.rank}. ${result.entryName} (${result.playerName}) - ${result.total} pts"
                    }
                    "League '${standings.league.name}':\n$rows"
                }.getOrNull()
            }.joinToString("\n\n").ifEmpty { "No league standings available." }
        } catch (e: Exception) {
            println("Error: $e")
            return ""
        }
    }

}


@Serializable
data class BuildSquadArgs(
    @property:LLMDescription("Number of upcoming gameweeks to optimise for. Scores players by season points PLUS fixture ease over this window, so form and an easy run both count. Omit to rank by season points alone.")
    val lookaheadGameweeks: Int? = null
)

class BuildSquadTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<BuildSquadArgs>(
    argsType = typeToken<BuildSquadArgs>(),
    name = "buildSquad",
    description = "Pick an optimal 15-player FPL squad (2 GK/5 DEF/5 MID/3 FWD) within budget and max 3 players per club, " +
        "plus the best starting XI, formation and captain/vice-captain for the next gameweek"
) {
    override suspend fun execute(args: BuildSquadArgs): String {
        try {
            val players = fantasyPremierLeagueRepository.getPlayers().first()
            val fixtures = fantasyPremierLeagueRepository.getFixtures().first()
            val currentGameweek = fantasyPremierLeagueRepository.currentGameweek.value
            val gameSettings = fantasyPremierLeagueRepository.gameSettings.value
            val squadTotalSpend = gameSettings?.squad_total_spend ?: 1000
            val clubLimit = gameSettings?.squad_team_limit ?: 3

            val selection = pickSquad(
                players = players,
                fixtures = fixtures,
                currentGameweek = currentGameweek,
                squadTotalSpend = squadTotalSpend,
                clubLimit = clubLimit,
                lookaheadGameweeks = args.lookaheadGameweeks,
            )
            val startingXI = pickStartingXI(selection.players, fixtures, currentGameweek)

            val positionName = mapOf(1 to "GK", 2 to "DEF", 3 to "MID", 4 to "FWD")
            fun describe(player: Player): String {
                val role = when (player.id) {
                    startingXI.captain.id -> " (C)"
                    startingXI.viceCaptain.id -> " (VC)"
                    else -> ""
                }
                return "id=${player.id} ${player.name} (${positionName[player.elementType]}, ${player.team}) " +
                    "£${player.currentPrice}m, ${player.points} pts$role"
            }

            val formation = listOf(2, 3, 4).joinToString("-") { elementType ->
                startingXI.startingXI.count { it.elementType == elementType }.toString()
            }
            val squadLines = selection.players.sortedBy { it.elementType }.joinToString("\n") { describe(it) }
            val startingXILines = startingXI.startingXI.sortedBy { it.elementType }.joinToString("\n") { describe(it) }
            val benchLines = startingXI.bench.sortedBy { it.elementType }.joinToString("\n") { describe(it) }

            return """
                Squad (15):
                $squadLines

                Total cost: £${selection.totalCost}m / £${squadTotalSpend / 10.0}m budget

                Starting XI ($formation) for gameweek $currentGameweek:
                $startingXILines

                Bench:
                $benchLines

                Captain: ${startingXI.captain.name} (id=${startingXI.captain.id})
                Vice-captain: ${startingXI.viceCaptain.name} (id=${startingXI.viceCaptain.id})
            """.trimIndent()
        } catch (e: Exception) {
            println("Error: $e")
            return ""
        }
    }
}
