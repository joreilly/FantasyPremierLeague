package dev.johnoreilly.common

import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.di.initKoin
import kotlinx.datetime.*

suspend fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin

    val api = koin.get<FantasyPremierLeagueApi>()
    val staticInfo = api.fetchBootstrapStaticInfo()
    val fixtures = api.fetchFixtures()

    
    println("Positions")
    staticInfo.element_types.forEach {
        println("${it.id}: ${it.singular_name_short}")
    }


    println("Events")
    staticInfo.events.forEach {
        println("${it.id}, ${it.name}")
    }

    val currentEvent = staticInfo.events.find { it.is_current == true }
    val nextEvent = staticInfo.events.find { it.is_next == true }

    println("next game week fixtures")
    val nextFixtures = fixtures.filter { it.event == nextEvent?.id }
    nextFixtures.forEach { fixture ->

        val datetimeInSystemZone = fixture.kickoff_time?.toLocalDateTime(TimeZone.currentSystemDefault())

        val homeTeam = staticInfo.teams.find { it.id == fixture.team_h }
        val awayTeam = staticInfo.teams.find { it.id == fixture.team_a }
        if (homeTeam != null && awayTeam != null) {
            println("${fixture.event}: $datetimeInSystemZone ${fixture.team_h_difficulty}:${fixture.team_a_difficulty}: ${homeTeam.name} vs ${awayTeam.name}")
        }
    }

    val players = staticInfo.elements
    println("Players")
    players.sortedByDescending { it.total_points }.take(10).forEach { element ->
        val position = staticInfo.element_types.find { it.id == element.element_type }
        println("${element.first_name} ${element.second_name}: ${element.now_cost/10.0} ${element.total_points} ${position?.singular_name_short}")
    }


}
