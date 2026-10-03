package dev.johnoreilly.common.di

import dev.johnoreilly.common.AppSettings
import dev.johnoreilly.common.agent.AgentProvider
import dev.johnoreilly.common.agent.FantasyPremierLeagueAgentProvider
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.platformModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(enableNetworkLogs = enableNetworkLogs))
    }

// called by iOS etc
fun initKoin() {
    initKoin(enableNetworkLogs = false, appDeclaration = {})
}

@HiddenFromObjC
fun commonModule(enableNetworkLogs: Boolean) = module {
    single { createJson() }
    single { createHttpClient(get(), get(), enableNetworkLogs = enableNetworkLogs) }

    single { FantasyPremierLeagueRepository() }
    single { FantasyPremierLeagueApi(get()) }

    single { AppSettings(get()) }

    single<AgentProvider> { FantasyPremierLeagueAgentProvider(get()) }

    includes(viewModelModule)
    includes(platformModule())
}

internal fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }

internal fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json, enableNetworkLogs: Boolean) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }
}
