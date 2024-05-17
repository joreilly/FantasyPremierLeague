package dev.johnoreilly.common.data.repository

import dev.johnoreilly.common.AppSettings
import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.model.EventStatusListDto
import dev.johnoreilly.common.data.model.FixtureDto
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.PlayerPastHistory
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class FantasyPremierLeagueRepository : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()
    private val database: AppDatabase by inject()
    private val appSettings: AppSettings by inject()

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val leagues = appSettings.leagues

    private var _currentGameweek: MutableStateFlow<Int> = MutableStateFlow(1)
    val currentGameweek = _currentGameweek.asStateFlow()

    init {
        coroutineScope.launch {
            loadData()
        }
    }

    private suspend fun loadData() {
        try {
            val bootstrapStaticInfoDto = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()
            val fixtures = fantasyPremierLeagueApi.fetchFixtures()
            writeDataToDb(bootstrapStaticInfoDto, fixtures)
        } catch (e: Exception) {
            // TODO surface this to UI/option to retry etc ?
            println("Exception reading data: $e")
        }
    }


    private suspend fun writeDataToDb(
        bootstrapStaticInfoDto: BootstrapStaticInfoDto,
        fixtures: List<FixtureDto>
    ) {
        //store current gameweek
        _currentGameweek.value =
            bootstrapStaticInfoDto.events.firstOrNull { it.is_current }?.id ?: 1

        // store teams
        val teamList = bootstrapStaticInfoDto.teams.mapIndexed { teamIndex, teamDto ->
            Team(teamDto.id, teamIndex + 1, teamDto.name, teamDto.code)
        }
        database.fantasyPremierLeagueDao().insertTeamList(teamList)


        // store players
        val playerList = bootstrapStaticInfoDto.elements.map { playerDto ->
            val playerName = "${playerDto.first_name} ${playerDto.second_name}"
            val playerImageUrl = "https://resources.premierleague.com/premierleague/photos/players/110x140/p${playerDto.code}.png"
            val teamName = teamList.find { team -> team.code == playerDto.team_code }?.name ?: ""
            val currentPrice = playerDto.now_cost / 10.0

            Player(
                playerDto.id,
                playerName,
                teamName,
                playerImageUrl,
                playerDto.total_points,
                currentPrice,
                playerDto.goals_scored,
                playerDto.assists
            )
        }
        database.fantasyPremierLeagueDao().insertPlayerList(playerList)

        // store fixtures
        val fixtureList = fixtures.map { fixtureDto ->
            val homeTeam = teamList.find() { team -> team.index == fixtureDto.team_h }
            val awayTeam = teamList.find() { team -> team.index == fixtureDto.team_a }

            val homeTeamName = homeTeam?.name ?: ""
            val awayTeamName = awayTeam?.name ?: ""
            val homeTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${homeTeam?.code}.png"
            val awayTeamPhotoUrl = "https://resources.premierleague.com/premierleague/badges/t${awayTeam?.code}.png"

            val localKickoffTime = fixtureDto.kickoff_time.toString().toInstant()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            GameFixture(
                fixtureDto.id,
                localKickoffTime,
                homeTeamName,
                awayTeamName,
                homeTeamPhotoUrl,
                awayTeamPhotoUrl,
                fixtureDto.team_h_score,
                fixtureDto.team_a_score,
                fixtureDto.event ?: 0
            )
        }
        database.fantasyPremierLeagueDao().insertFixtureList(fixtureList)
    }


    fun getPlayers(): Flow<List<Player>> {
        return database.fantasyPremierLeagueDao().getPlayerListAsFlow()
    }

    suspend fun getPlayer(id: Int): Player {
        return database.fantasyPremierLeagueDao().getPlayer(id)
    }


    fun getFixtures(): Flow<List<GameFixture>> {
        return database.fantasyPremierLeagueDao().getFixtureListAsFlow()
    }

    suspend fun getFixture(id: Int): GameFixture {
        return database.fantasyPremierLeagueDao().getFixture(id)
    }

    suspend fun getPlayerHistoryData(playerId: Int): List<PlayerPastHistory> {
        return fantasyPremierLeagueApi.fetchPlayerData(playerId).history_past.map {
            PlayerPastHistory(it.season_name, it.total_points)
        }
    }

    suspend fun getLeagueStandings(leagueId: Int): LeagueStandingsDto {
        return fantasyPremierLeagueApi.fetchLeagueStandings(leagueId)
    }

    suspend fun getEventStatus(): EventStatusListDto {
        return fantasyPremierLeagueApi.fetchEventStatus()
    }

    suspend fun updateLeagues(leagues: List<String>) {
        appSettings.updatesLeaguesSetting(leagues)
    }
}
