package dev.johnoreilly.common.remote

import dev.johnoreilly.common.model.BootstrapStaticInfo
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.KoinComponent

@Serializable
data class Fixture(val kickoff_time: String?, val team_h: Int, val team_a: Int)

class FantasyPremierLeagueApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://fantasy.premierleague.com/api",
) : KoinComponent {
    suspend fun fetchBootstrapStaticInfo() = client.get<BootstrapStaticInfo>("$baseUrl/bootstrap-static/")
    suspend fun fetchFixtures() = client.get<List<Fixture>>("$baseUrl/fixtures")
}
