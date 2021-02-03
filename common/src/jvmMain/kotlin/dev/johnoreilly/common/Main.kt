package com.surrus

import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import kotlinx.coroutines.runBlocking
import org.nield.kotlinstatistics.descriptiveStatistics

fun main() {
    runBlocking {
        val koin = initKoin(enableNetworkLogs = true).koin

        val repository = koin.get<FantasyPremierLeagueRepository>()
        val api = koin.get<FantasyPremierLeagueApi>()
        val staticInfo = api.fetchBootstrapStaticInfo()

        val fixtures = repository.fetchPastFixtures()

        fixtures.forEach { fixture ->
            val homeTeam = staticInfo.teams.find { it.id == fixture.team_h }
            val awayTeam = staticInfo.teams.find { it.id == fixture.team_a }
            if (homeTeam != null && awayTeam != null) {
                println("${fixture.kickoff_time}: ${homeTeam.name} vs ${awayTeam.name}")
            }
        }

        val players = staticInfo.elements
        println(players.size)

        val descriptives = staticInfo.elements.map { it.total_points }.descriptiveStatistics
        println(descriptives.standardDeviation)

        staticInfo.elements.sortedByDescending { it.total_points }.take(5).forEach { element ->
            println("${element.first_name} ${element.second_name}: ${element.now_cost/10.0} ${element.total_points}")
        }
    }
}
