package dev.johnoreilly.common.repository

import dev.johnoreilly.common.entities.GameFixture
import dev.johnoreilly.common.entities.Player
import dev.johnoreilly.common.getLogger
import dev.johnoreilly.common.model.BootstrapStaticInfo
import dev.johnoreilly.common.remote.FantasyPremierLeagueApi
import org.koin.core.KoinComponent
import org.koin.core.inject

class FantasyPremierLeagueRepository : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()
    private var bootstrapStaticInfo: BootstrapStaticInfo? = null

    suspend fun getBootstrapStaticInfo(): BootstrapStaticInfo {
        // TEMP local in memory cache until we add persistence
        if (bootstrapStaticInfo == null) {
            bootstrapStaticInfo = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()
        }
        return bootstrapStaticInfo!!
    }

    suspend fun fetchFixtures() = fantasyPremierLeagueApi.fetchFixtures().filter { it.kickoff_time != null }

    @Throws(Exception::class)
    suspend fun getPlayers(): List<Player> {
        val bootstrapStaticInfo = getBootstrapStaticInfo()

        return bootstrapStaticInfo.elements
            .sortedByDescending { it.total_points }
            .map { player ->
                val playerName = "${player.first_name} ${player.second_name}"
                val playerImageUrl = "https://resources.premierleague.com/premierleague/photos/players/110x140/p${player.code}.png"
                val team = bootstrapStaticInfo.teams.find { it.code == player.team_code }
                val teamName = team?.name ?: ""
                val currentPrice = player.now_cost / 10.0

                Player(
                    player.id, playerName, teamName, playerImageUrl,
                    player.total_points, currentPrice, player.goals_scored, player.assists
                )
            }
    }

    @Throws(Exception::class)
    suspend fun getFixtures(): List<GameFixture> {
        val bootstrapStaticInfo = getBootstrapStaticInfo()

        return fetchFixtures().map { fixture ->
            val homeTeam = bootstrapStaticInfo.teams[fixture.team_h-1]
            val homeTeamName = homeTeam.name
            val homeTeamCode = homeTeam.code
            val homeTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${homeTeamCode}.png"

            val awayTeamCode = bootstrapStaticInfo.teams[fixture.team_a-1].code
            val awayTeamName = bootstrapStaticInfo.teams[fixture.team_a-1].name
            val awayTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${awayTeamCode}.png"

            GameFixture(
                fixture.id,
                fixture.kickoff_time ?: "",
                homeTeamName,
                awayTeamName,
                homeTeamPhotoUrl,
                awayTeamPhotoUrl
            )
        }
    }
}