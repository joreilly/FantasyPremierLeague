package dev.johnoreilly.common.remote

import dev.johnoreilly.common.model.BootstrapStaticInfo
import dev.johnoreilly.common.model.Fixture
import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.KoinComponent


class FantasyPremierLeagueApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://fantasy.premierleague.com/api",
) : KoinComponent {
    suspend fun fetchBootstrapStaticInfo() = client.get<BootstrapStaticInfo>("$baseUrl/bootstrap-static/")
    suspend fun fetchFixtures() = client.get<List<Fixture>>("$baseUrl/fixtures")
}
