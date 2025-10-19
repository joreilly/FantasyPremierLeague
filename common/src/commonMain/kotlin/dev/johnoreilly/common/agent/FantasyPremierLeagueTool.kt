package dev.johnoreilly.climatetrace.agent

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.Tool
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import kotlinx.coroutines.flow.first


class GetPlayersTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Tool.EmptyArgs>() {
    override val argsSerializer = EmptyArgs.serializer()
    override val description = "Get the list of players"

    private var playerList: List<Player>? = null

    override suspend fun doExecute(args: EmptyArgs): String {
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


class GetFixturesTool(val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) : SimpleTool<Tool.EmptyArgs>() {
    override val argsSerializer = EmptyArgs.serializer()
    override val description = "Get the list of fixtures"

    override suspend fun doExecute(args: EmptyArgs): String {
        try {
            val fixtures = fantasyPremierLeagueRepository.getFixtures().first()
            return fixtures.toString()
        } catch (e: Exception) {
            println("Error: $e")
            return ""
        }
    }

}