package dev.johnoreilly.common.repository

import dev.johnoreilly.common.remote.FantasyPremierLeagueApi
import org.koin.core.KoinComponent
import org.koin.core.inject

data class Player(val id: Int, val name: String, val team: String, val photoUrl: String, val points: Int)

class FantasyPremierLeagueRepository  : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()

    suspend fun fetchBootstrapStaticInfo() = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()

    suspend fun fetchFixtures() = fantasyPremierLeagueApi.fetchFixtures().filter { it.kickoff_time != null }

    @Throws(Exception::class)
    suspend fun getPlayers(): List<Player> {
        // TODO persist this data and drive UI from database
        val bootstrapStaticInfo = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()

        return bootstrapStaticInfo.elements
            .sortedByDescending { it.total_points }
            .map { player ->
            val playerName = "${player.first_name} ${player.second_name}"
            val playerImageUrl = "https://resources.premierleague.com/premierleague/photos/players/110x140/p${player.code}.png"
            val team = bootstrapStaticInfo.teams.find { it.code == player.team_code }
            val teamName = team?.name ?: ""

            Player(player.id, playerName, teamName, playerImageUrl, player.total_points)
        }
    }
}