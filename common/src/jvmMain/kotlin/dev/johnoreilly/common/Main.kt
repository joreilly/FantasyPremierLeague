package dev.johnoreilly.common

import dev.johnoreilly.common.data.model.ElementDto
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import dev.johnoreilly.common.di.initKoin
import kotlinx.datetime.*
import org.ojalgo.okalgo.*
import org.ojalgo.optimisation.Variable

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



    expressionsbasedmodel {

        val playerVariableList = mutableListOf<Variable>()

        val maximiseExpression = ExpressionBuilder()
        val costConstraint = ExpressionBuilder()
        val numberOfPlayersConstraint = ExpressionBuilder()
        val numberGoalkeepersConstraint = ExpressionBuilder()
        val numberDefendersConstraint = ExpressionBuilder()
        val numberMidfieldersConstraint = ExpressionBuilder()
        val numberForwardsConstraint = ExpressionBuilder()

        players.forEach { player ->
            val playerVariable = variable(name = player.id.toString(), isBinary = true)
            playerVariableList.add(playerVariable)

            maximiseExpression.plus(player.total_points*playerVariable)
            costConstraint.plus(player.now_cost*playerVariable)
            numberOfPlayersConstraint.plus(playerVariable)

            if (player.element_type == 1) {
                numberGoalkeepersConstraint.plus(playerVariable)
            } else if (player.element_type == 2) {
                numberDefendersConstraint.plus(playerVariable)
            } else if (player.element_type == 3) {
                numberMidfieldersConstraint.plus(playerVariable)
            } else if (player.element_type == 4) {
                numberForwardsConstraint.plus(playerVariable)
            }
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
        }

        maximise().run(::println)

        val optimizedTeam = playerVariableList.filter { it.value.toInt() == 1 }.map { playerVariable ->
            players.find { it.id.toString() == playerVariable.name }
        }.filterNotNull()

        optimizedTeam.sortedBy { it.element_type }.forEach { player ->
            println(player.web_name + " " + player.total_points + " " + player.element_type)
        }

    }

}
