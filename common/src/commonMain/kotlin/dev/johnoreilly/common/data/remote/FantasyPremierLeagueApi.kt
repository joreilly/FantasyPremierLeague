package dev.johnoreilly.common.data.remote

import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.model.FixtureDto
import dev.johnoreilly.common.data.model.GameWeekLiveDataDto
import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent


class FantasyPremierLeagueApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://fantasy.premierleague.com/api",
) : KoinComponent {

    suspend fun fetchBootstrapStaticInfo() = client.get<BootstrapStaticInfoDto>("$baseUrl/bootstrap-static/")
    suspend fun fetchFixtures() = client.get<List<FixtureDto>>("$baseUrl/fixtures")
    suspend fun fetchUpcomingFixtures() = client.get<List<FixtureDto>>("$baseUrl/fixtures?future=1")
    suspend fun fetchGameWeekLiveData(eventId: Int) = client.get<GameWeekLiveDataDto>("$baseUrl/event/$eventId/live/")
}
