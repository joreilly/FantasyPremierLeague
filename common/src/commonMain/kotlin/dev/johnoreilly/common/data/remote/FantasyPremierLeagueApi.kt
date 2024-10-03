package dev.johnoreilly.common.data.remote

import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.model.ElementSummaryDto
import dev.johnoreilly.common.data.model.EventStatusListDto
import dev.johnoreilly.common.data.model.FixtureDto
import dev.johnoreilly.common.data.model.GameWeekLiveDataDto
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent


class FantasyPremierLeagueApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://fantasy.premierleague.com/api",
) : KoinComponent {

    private suspend inline fun <reified T> fetchData(endpoint: String): T {
        return client.get("$baseUrl/$endpoint").body()
    }

    suspend fun fetchBootstrapStaticInfo() = fetchData<BootstrapStaticInfoDto>("bootstrap-static/")
    suspend fun fetchFixtures() = fetchData<List<FixtureDto>>("fixtures")
    suspend fun fetchUpcomingFixtures() = fetchData<List<FixtureDto>>("fixtures?future=1")
    suspend fun fetchGameWeekLiveData(eventId: Int) = fetchData<GameWeekLiveDataDto>("event/$eventId/live/")
    suspend fun fetchPlayerData(playerId: Int) = fetchData<ElementSummaryDto>("element-summary/$playerId/")
    suspend fun fetchLeagueStandings(leagueId: Int) = fetchData<LeagueStandingsDto>("leagues-classic/$leagueId/standings/")
    suspend fun fetchEventStatus() = fetchData<EventStatusListDto>("event-status/")
}

