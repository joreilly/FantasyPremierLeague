package dev.johnoreilly.common

import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.di.initKoin
import kotlinx.datetime.*
import kotlin.time.Duration

suspend fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin

    val api = koin.get<FantasyPremierLeagueApi>()
    val staticInfo = api.fetchBootstrapStaticInfo()
    val fixtures = api.fetchFixtures()


    val now = Clock.System.now()
    fixtures.forEach { fixture ->

        fixture.kickoff_time?.let { kickoffTime ->
            val durationTillKickOff: Duration = kickoffTime - now
            if (durationTillKickOff.inWholeSeconds > 0 && durationTillKickOff.inWholeDays < 7) {
                val datetimeInSystemZone: LocalDateTime = kickoffTime.toLocalDateTime(TimeZone.currentSystemDefault())

                val homeTeam = staticInfo.teams.find { it.id == fixture.team_h }
                val awayTeam = staticInfo.teams.find { it.id == fixture.team_a }
                if (homeTeam != null && awayTeam != null) {
                    println("$datetimeInSystemZone ${fixture.team_h_difficulty}:${fixture.team_a_difficulty}  $durationTillKickOff ${fixture.kickoff_time}: ${homeTeam.name} vs ${awayTeam.name}")
                }
            }
        }
    }

    val players = staticInfo.elements
    println(players.size)

    staticInfo.elements.sortedByDescending { it.total_points }.take(5).forEach { element ->
        println("${element.first_name} ${element.second_name}: ${element.now_cost/10.0} ${element.total_points}")
    }
}
