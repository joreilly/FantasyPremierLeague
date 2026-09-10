package dev.johnoreilly.common.data.repository

import dev.johnoreilly.common.AppSettings
import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.model.EventStatusListDto
import dev.johnoreilly.common.data.model.FixtureDto
import dev.johnoreilly.common.data.model.GameSettingsDto
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.GameRules
import dev.johnoreilly.common.model.Gameweek
import dev.johnoreilly.common.model.PlayerPastHistory
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PositionRule
import dev.johnoreilly.common.model.Team
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.time.ExperimentalTime


class FantasyPremierLeagueRepository : KoinComponent {
    private val fantasyPremierLeagueApi: FantasyPremierLeagueApi by inject()
    private val database: AppDatabase by inject()
    private val appSettings: AppSettings by inject()

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    val leagues = appSettings.leagues

    private var _currentGameweek: MutableStateFlow<Int> = MutableStateFlow(1)
    val currentGameweek = _currentGameweek.asStateFlow()

    private var _gameSettings: MutableStateFlow<GameSettingsDto?> = MutableStateFlow(null)
    val gameSettings = _gameSettings.asStateFlow()

    private var _nextGameweek: MutableStateFlow<Int> = MutableStateFlow(1)
    val nextGameweek = _nextGameweek.asStateFlow()

    private var _gameweeks: MutableStateFlow<List<Gameweek>> = MutableStateFlow(emptyList())
    val gameweeks = _gameweeks.asStateFlow()

    private var _gameRules: MutableStateFlow<GameRules?> = MutableStateFlow(null)
    val gameRules = _gameRules.asStateFlow()

    // The first load runs in the background, which is invisible in a long-lived app but not in a
    // short-lived process (the MCP server) that may call in before the bootstrap fetch has landed
    // and read a still-default gameweek. Consumers that need real data await this first.
    private val initialLoad = CompletableDeferred<Unit>()

    init {
        coroutineScope.launch {
            loadData()
        }
    }

    /** Suspends until the first bootstrap load has finished, successfully or not. */
    suspend fun awaitInitialLoad() = initialLoad.await()

    private suspend fun loadData() {
        try {
            val bootstrapStaticInfoDto = fantasyPremierLeagueApi.fetchBootstrapStaticInfo()
            val fixtures = fantasyPremierLeagueApi.fetchFixtures()
            // region -> country name, used to populate player nationality (best-effort)
            // region -> country name, used to populate player nationality (best-effort)
            val regionNames = runCatching { fantasyPremierLeagueApi.fetchRegions() }
                .getOrDefault(emptyList())
                .associate { it.id to it.name }
            writeDataToDb(bootstrapStaticInfoDto, fixtures, regionNames)
        } catch (e: Exception) {
            // TODO surface this to UI/option to retry etc ?
            //println("Exception reading data: $e")
        } finally {
            initialLoad.complete(Unit)
        }
    }


    @OptIn(ExperimentalTime::class)
    private suspend fun writeDataToDb(
        bootstrapStaticInfoDto: BootstrapStaticInfoDto,
        fixtures: List<FixtureDto>,
        regionNames: Map<Int, String>
    ) {
        //store current gameweek
        _currentGameweek.value =
            bootstrapStaticInfoDto.events.firstOrNull { it.is_current }?.id ?: 1

        // Falls back to current + 1 rather than 1: between the last match of a gameweek and the
        // rollover, FPL reports no is_next event at all.
        _nextGameweek.value = bootstrapStaticInfoDto.events.firstOrNull { it.is_next }?.id
            ?: (_currentGameweek.value + 1)

        _gameSettings.value = bootstrapStaticInfoDto.game_settings

        _gameweeks.value = bootstrapStaticInfoDto.events.map { event ->
            Gameweek(
                id = event.id,
                name = event.name,
                deadlineTime = event.deadline_time,
                isCurrent = event.is_current,
                isNext = event.is_next,
                finished = event.finished,
                averageEntryScore = event.average_entry_score,
                highestScore = event.highest_score
            )
        }

        val gameSettings = bootstrapStaticInfoDto.game_settings
        _gameRules.value = GameRules(
            budget = gameSettings.squad_total_spend / 10.0,
            squadSize = gameSettings.squad_squadsize,
            startingXiSize = gameSettings.squad_squadplay,
            clubLimit = gameSettings.squad_team_limit,
            sellOnFeePercent = gameSettings.transfers_sell_on_fee * 100,
            formDays = gameSettings.stats_form_days,
            positions = bootstrapStaticInfoDto.element_types.map { elementType ->
                PositionRule(
                    elementType = elementType.id,
                    name = elementType.singular_name,
                    shortName = elementType.singular_name_short,
                    squadSelect = elementType.squad_select,
                    minPlay = elementType.squad_min_play,
                    maxPlay = elementType.squad_max_play
                )
            }
        )

        // store teams
        val teamList = bootstrapStaticInfoDto.teams.mapIndexed { teamIndex, teamDto ->
            Team(teamDto.id, teamIndex + 1, teamDto.name, teamDto.code, teamDto.short_name)
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
                playerDto.assists,
                playerDto.region?.let { regionNames[it] },
                playerDto.element_type,
                playerDto.team,
                playerDto.status,
                playerDto.chance_of_playing_next_round,
                // FPL sends these as strings; a missing/unparseable value is treated as "no signal"
                // rather than being allowed to fail the whole load.
                webName = playerDto.web_name,
                form = playerDto.form.toDoubleOrNull() ?: 0.0,
                pointsPerGame = playerDto.points_per_game.toDoubleOrNull() ?: 0.0,
                minutes = playerDto.minutes,
                selectedByPercent = playerDto.selected_by_percent.toDoubleOrNull() ?: 0.0,
                expectedPointsNext = playerDto.ep_next?.toDoubleOrNull(),
                news = playerDto.news,
                bonus = playerDto.bonus,
                bps = playerDto.bps,
                cleanSheets = playerDto.clean_sheets,
                goalsConceded = playerDto.goals_conceded,
                saves = playerDto.saves,
                penaltiesOrder = playerDto.penalties_order,
                directFreekicksOrder = playerDto.direct_freekicks_order,
                cornersOrder = playerDto.corners_and_indirect_freekicks_order,
                expectedGoalInvolvementsPer90 = playerDto.expected_goal_involvements_per_90
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


                //.toString().toInstant(TimeZone.currentSystemDefault())
            val localKickoffTime = fixtureDto.kickoff_time?.toLocalDateTime(TimeZone.currentSystemDefault())

            GameFixture(
                fixtureDto.id,
                localKickoffTime,
                homeTeamName,
                awayTeamName,
                homeTeamPhotoUrl,
                awayTeamPhotoUrl,
                fixtureDto.team_h_score,
                fixtureDto.team_a_score,
                fixtureDto.event ?: 0,
                fixtureDto.team_h,
                fixtureDto.team_a,
                fixtureDto.team_h_difficulty,
                fixtureDto.team_a_difficulty
            )
        }
        database.fantasyPremierLeagueDao().insertFixtureList(fixtureList)
    }


    fun getTeams(): Flow<List<Team>> {
        return database.fantasyPremierLeagueDao().getTeamListAsFlow()
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
