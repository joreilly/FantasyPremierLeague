package dev.johnoreilly.common.data.repository

import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


class FantasyPremierLeagueRepository : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()
    private var bootstrapStaticInfoDto: BootstrapStaticInfoDto? = null

    private suspend fun getBootstrapStaticInfo(): BootstrapStaticInfoDto {
        // TEMP local in memory cache until we add persistence
        if (bootstrapStaticInfoDto == null) {
            bootstrapStaticInfoDto = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()
        }
        return bootstrapStaticInfoDto!!
    }

    suspend fun fetchPastFixtures() = fantasyPremierLeagueApi
        .fetchFixtures()
        .filter { it.kickoff_time != null }
        .filter { it.team_h_score != null }
        .filter { it.team_a_score != null }

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
    suspend fun getPastFixtures(): List<GameFixture> {
        val bootstrapStaticInfo = getBootstrapStaticInfo()

        return fetchPastFixtures().map { fixture ->
            val homeTeam = bootstrapStaticInfo.teams[fixture.team_h-1]
            val homeTeamName = homeTeam.name
            val homeTeamCode = homeTeam.code
            val homeTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${homeTeamCode}.png"
            val homeTeamScore = fixture.team_h_score ?: 0

            val awayTeamCode = bootstrapStaticInfo.teams[fixture.team_a-1].code
            val awayTeamName = bootstrapStaticInfo.teams[fixture.team_a-1].name
            val awayTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${awayTeamCode}.png"
            val awayTeamScore = fixture.team_a_score ?: 0

            fixture.kickoff_time?.let {
                val localKickoffTime = it.toInstant().toLocalDateTime(TimeZone.currentSystemDefault())

                GameFixture(
                    fixture.id,
                    localKickoffTime,
                    homeTeamName,
                    awayTeamName,
                    homeTeamPhotoUrl,
                    awayTeamPhotoUrl,
                    homeTeamScore,
                    awayTeamScore
                )
            }
        }.filterNotNull()
    }
}