package dev.johnoreilly.common

import dev.johnoreilly.common.data.model.BootstrapStaticInfoDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.di.initKoin
import kotlinx.datetime.*
import org.ojalgo.okalgo.*
import org.ojalgo.optimisation.Variable

suspend fun main() {
    val koin = initKoin(enableNetworkLogs = true).koin

    val api = koin.get<FantasyPremierLeagueApi>()

    // https://medium.com/@frenzelts/fantasy-premier-league-api-endpoints-a-detailed-guide-acbd5598eb19

    val eventStatus = api.fetchEventStatus()
    println(eventStatus)



    val staticInfo = api.fetchBootstrapStaticInfo()
    val fixtures = api.fetchFixtures()


    println("Positions")
    staticInfo.element_types.forEach {
        println("${it.id}: ${it.singular_name_short}")
    }


    println("Events")
    staticInfo.events.forEach {
        println("${it.id}, ${it.name} ${it.deadline_time}")
    }

    val currentEvent = staticInfo.events.find { it.is_current }
    val nextEvent = staticInfo.events.find { it.is_next }

    println("next game week fixtures")
    val nextFixtures = fixtures.filter { it.event == nextEvent?.id }
    nextFixtures.forEach { fixture ->

        val datetimeInSystemZone =
            fixture.kickoff_time?.toLocalDateTime(TimeZone.currentSystemDefault())

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
        val team = staticInfo.teams.find { it.id == element.team }
        println("${element.first_name} ${element.second_name}: ${element.now_cost / 10.0} ${element.total_points} ${position?.singular_name_short}, team=${team?.name}")
    }


    currentEvent?.let {
        val gameWeekLiveData = api.fetchGameWeekLiveData(currentEvent.id)

        println("Game week player info")
        gameWeekLiveData.elements.sortedByDescending { it.stats.total_points }.take(10)
            .forEach { gameWeekElement ->
                players.find { it.id == gameWeekElement.id }?.let { element ->
                    println("${element.web_name}: ${element.now_cost / 10.0} ${element.total_points} ${gameWeekElement.stats}")
                }
            }
    }


    pickTeam(staticInfo)
}

/*
    Use okAlgo library to pick team
 */
fun pickTeam(staticInfo: BootstrapStaticInfoDto) {


    expressionsbasedmodel {

        val playerVariableList = mutableListOf<Variable>()

        val maximiseExpression = ExpressionBuilder()
        val costConstraint = ExpressionBuilder()
        val numberOfPlayersConstraint = ExpressionBuilder()
        val numberGoalkeepersConstraint = ExpressionBuilder()
        val numberDefendersConstraint = ExpressionBuilder()
        val numberMidfieldersConstraint = ExpressionBuilder()
        val numberForwardsConstraint = ExpressionBuilder()

        val players = staticInfo.elements
        players.forEach { player ->
            val playingThisRound  = player.chance_of_playing_next_round == 100
            if (playingThisRound) {

                val playerVariable = variable(name = player.id.toString(), isBinary = true)
                playerVariableList.add(playerVariable)

                maximiseExpression.plus(player.total_points * playerVariable)
                costConstraint.plus(player.now_cost * playerVariable)
                numberOfPlayersConstraint.plus(playerVariable)

                when (player.element_type) {
                    1 -> numberGoalkeepersConstraint.plus(playerVariable)
                    2 -> numberDefendersConstraint.plus(playerVariable)
                    3 -> numberMidfieldersConstraint.plus(playerVariable)
                    4 -> numberForwardsConstraint.plus(playerVariable)
                }
            }
        }

        // team constraints
        val teamConstraintList = mutableListOf<ExpressionBuilder>()
        staticInfo.teams.forEach { team ->
            val teamConstraint = ExpressionBuilder()

            players.forEach { player ->
                if (player.team == team.id) {
                    val playerVariable = playerVariableList.find { it.name == player.id.toString() }
                    playerVariable?.let {
                        teamConstraint.plus(playerVariable)
                    }
                }
            }
            teamConstraint.GTE(3)
            teamConstraintList.add(teamConstraint)
        }


        costConstraint.GTE(1000)
        numberOfPlayersConstraint.EQ(15)
        numberGoalkeepersConstraint.EQ(2)
        numberDefendersConstraint.EQ(5)
        numberMidfieldersConstraint.EQ(5)
        numberForwardsConstraint.EQ(3)


        expression(maximiseExpression) {
            weight(1)
        }

        expression {
            set(costConstraint)
            set(numberOfPlayersConstraint)

            set(numberGoalkeepersConstraint)
            set(numberDefendersConstraint)
            set(numberMidfieldersConstraint)
            set(numberForwardsConstraint)
            teamConstraintList.forEach {
                set(it)
            }
        }

        maximise().run(::println)

        val optimizedTeam = playerVariableList.filter { it.value.toInt() == 1 }.mapNotNull { playerVariable ->
            players.find { it.id.toString() == playerVariable.name }
        }

        optimizedTeam.sortedBy { it.element_type }.forEach { player ->
            val team = staticInfo.teams.find { it.id == player.team }
            println("${player.web_name} ${player.now_cost} ${player.total_points} ${player.element_type}, team=${team?.name}, ${player.chance_of_playing_this_round}")
        }

        val totalPoints = optimizedTeam.sumOf { it.total_points }
        println("total points = $totalPoints")

        val totalCost = optimizedTeam.sumOf { it.now_cost }
        println("total cost = $totalCost")

    }

}

