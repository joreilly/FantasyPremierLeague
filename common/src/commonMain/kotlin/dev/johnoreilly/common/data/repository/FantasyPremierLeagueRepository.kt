package dev.johnoreilly.common.data.repository

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import dev.johnoreilly.common.AppSettings
import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.model.EventStatusListDto
import dev.johnoreilly.common.data.model.FixtureDto
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import dev.johnoreilly.common.domain.entities.Team
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TeamDb: RealmObject {
    @PrimaryKey
    var id: Int = 0
    var index: Int = 0
    var name: String = ""
    var code: Int = 0
}

class PlayerDb: RealmObject {
    @PrimaryKey
    var id: Int = 0
    var firstName: String = ""
    var secondName: String = ""
    var code: Int = 0
    var teamCode: Int = 0
    var totalPoints: Int = 0
    var nowCost: Int = 0
    var goalsScored: Int = 0
    var assists: Int = 0
    var team: TeamDb? = null
}

class FixtureDb: RealmObject {
    @PrimaryKey
    var id: Int = 0
    var kickoffTime: String? = ""
    var homeTeam: TeamDb? = null
    var awayTeam: TeamDb? = null
    var homeTeamScore: Int = 0
    var awayTeamScore: Int = 0
    var event: Int = 0
}

class FantasyPremierLeagueRepository : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()
    private val realm: Realm by inject()
    private val appSettings: AppSettings by inject()

    @NativeCoroutineScope
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _teamList = MutableStateFlow<List<Team>>(emptyList())
    val teamList = _teamList.asStateFlow()

    private val _playerList = MutableStateFlow<List<Player>>(emptyList())
    val playerList = _playerList.asStateFlow()

    private val _fixtureList = MutableStateFlow<List<GameFixture>>(emptyList())
    val fixtureList = _fixtureList.asStateFlow()

    private val _gameweekToFixtureMap = MutableStateFlow<Map<Int,List<GameFixture>>>(emptyMap())
    val gameweekToFixtures: StateFlow<Map<Int, List<GameFixture>>> = _gameweekToFixtureMap.asStateFlow()

    val leagues = appSettings.leagues


    init {
        coroutineScope.launch {
            loadData()

            launch {
                realm.query<TeamDb>().asFlow()
                    .map { it.list }
                    .collect { it: RealmResults<TeamDb> ->
                    _teamList.value = it.toList().map {
                        Team(it.id, it.index, it.name, it.code)
                    }
                }
            }

            launch {
                realm.query<PlayerDb>().asFlow()
                    .map { it.list }
                    .collect { it: RealmResults<PlayerDb> ->
                    _playerList.value = it.toList().map {
                        val playerName = "${it.firstName} ${it.secondName}"
                        val playerImageUrl = "https://resources.premierleague.com/premierleague/photos/players/110x140/p${it.code}.png"
                        val teamName = it.team?.name ?: ""
                        val currentPrice = it.nowCost / 10.0

                        Player(it.id, playerName, teamName, playerImageUrl, it.totalPoints, currentPrice, it.goalsScored, it.assists)
                    }
                }
            }

            launch {
                realm.query<FixtureDb>().asFlow()
                    .map { it.list }
                    .collect { it: RealmResults<FixtureDb> ->
                        _fixtureList.value = it.toList().mapNotNull {
                            val homeTeamName = it.homeTeam?.name ?: ""
                            val homeTeamCode = it.homeTeam?.code ?: 0
                            val homeTeamScore = it.homeTeamScore ?: 0
                            val homeTeamPhotoUrl =
                                "https://resources.premierleague.com/premierleague/badges/t${homeTeamCode}.png"

                            val awayTeamName = it.awayTeam?.name ?: ""
                            val awayTeamCode = it.awayTeam?.code ?: 0
                            val awayTeamScore = it.awayTeamScore ?: 0
                            val awayTeamPhotoUrl =
                                "https://resources.premierleague.com/premierleague/badges/t${awayTeamCode}.png"

                            it.kickoffTime?.let { kickoffTime ->
                                val localKickoffTime = kickoffTime.toInstant()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())

                                val gf = GameFixture(
                                    it.id,
                                    localKickoffTime,
                                    homeTeamName,
                                    awayTeamName,
                                    homeTeamPhotoUrl,
                                    awayTeamPhotoUrl,
                                    homeTeamScore,
                                    awayTeamScore,
                                    it.event
                                )

                                return@let gf
                            }
                        }
                        //Build gameweek to fixture map
                        _gameweekToFixtureMap.value = _fixtureList.value
                            .filter { it.event != null }
                            .groupBy { it.event!! }
                }
            }

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
    )  {

        realm.write {
            // store teams
            bootstrapStaticInfoDto.teams.forEachIndexed { teamIndex, teamDto ->
                copyToRealm(TeamDb().apply {
                    id = teamDto.id
                    index = teamIndex + 1
                    name = teamDto.name
                    code = teamDto.code
                }, updatePolicy = UpdatePolicy.ALL)
            }

            // store players
            bootstrapStaticInfoDto.elements.forEach { player ->
                copyToRealm(PlayerDb().apply {
                    id = player.id
                    firstName = player.first_name
                    secondName = player.second_name
                    code = player.code
                    teamCode = player.team_code
                    totalPoints = player.total_points
                    nowCost = player.now_cost
                    goalsScored = player.goals_scored
                    assists = player.assists

                    team = query<TeamDb>("code = $0", player.team_code).first().find()
                }, updatePolicy = UpdatePolicy.ALL)
            }

            // store fixtures
            val teams = query<TeamDb>().find().toList()
            fixtures.forEach { fixtureDto ->
                if (fixtureDto.kickoff_time != null) {
                    copyToRealm(FixtureDb().apply {
                        id = fixtureDto.id
                        kickoffTime = fixtureDto.kickoff_time.toString()
                        fixtureDto.event?.let { event = it }
                        fixtureDto.team_h_score?.let { homeTeamScore = it }
                        fixtureDto.team_a_score?.let { awayTeamScore = it }

                        homeTeam = teams.find { it.index == fixtureDto.team_h }
                        awayTeam = teams.find { it.index == fixtureDto.team_a }
                    }, updatePolicy = UpdatePolicy.ALL)
                }
            }

        }
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

    fun updateLeagues(leagues: List<String>) {
        appSettings.updatesLeagesSetting(leagues)
    }

}
