@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.fixtures.FixtureDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.ClubInFixtureView
import org.koin.compose.koinInject

@Composable
fun FixtureDetailsView(fixtureId: Int, popBackStack: () -> Unit) {
    val viewModel = koinInject<FixturesViewModel>()

    val fixture by produceState<GameFixture?>(initialValue = null) {
        value = viewModel.getFixture(fixtureId)
    }

    fixture?.let { fixture ->
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Fixture details")
                    },
                    navigationIcon = {
                        IconButton(onClick = { popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }) {
            Column(
                modifier = Modifier.padding(it).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClubInFixtureView(
                        fixture.homeTeam,
                        fixture.homeTeamPhotoUrl
                    )
                    Text(
                        text = "${fixture.homeTeamScore ?: ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        text = "vs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(
                        text = "${fixture.awayTeamScore ?: ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    ClubInFixtureView(
                        fixture.awayTeam,
                        fixture.awayTeamPhotoUrl
                    )
                }

                fixture.localKickoffTime.let { localKickoffTime ->
                    val formattedTime = "%02d:%02d".format(localKickoffTime.hour, localKickoffTime.minute)
                    PastFixtureStatView(statName = "Date", statValue = localKickoffTime.date.toString())
                    PastFixtureStatView(statName = "Kick Off Time", statValue = formattedTime)
                }
            }
        }
    }
}

@Composable
fun PastFixtureStatView(statName: String, statValue: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = statName,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text = statValue,
                    color = Color(0xFF3179EA),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(thickness = 1.dp)
    }
}