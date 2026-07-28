package dev.johnoreilly.common.agent

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.serialization.typeToken
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import kotlinx.coroutines.flow.first


class GetPlayersTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Unit>(
    argsType = typeToken<Unit>(),
    name = "getPlayers",
    description = "Get the list of players"
) {
    private var playerList: List<Player>? = null

    override suspend fun execute(args: Unit): String {
        try {
            if (playerList == null) {
                playerList = fantasyPremierLeagueRepository.getPlayers().first()
            }
            return playerList.toString()
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
