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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.ClubInFixtureView
import dev.johnoreilly.fantasypremierleague.presentation.global.Spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun FixtureDetailsView(fixtureId: Int, popBackStack: () -> Unit) {
    val viewModel = koinViewModel<FixturesViewModel>()

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
                        .padding(top = Spacing.mediumLarge),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClubInFixtureView(
                        fixture.homeTeam,
                        fixture.homeTeamPhotoUrl
                    )
                    Text(
                        text = "${fixture.homeTeamScore ?: ""}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${fixture.awayTeamScore ?: ""}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ClubInFixtureView(
                        fixture.awayTeam,
                        fixture.awayTeamPhotoUrl
                    )
                }

                fixture.localKickoffTime?.let { localKickoffTime ->
                    val formattedTime = "%02d:%02d".format(localKickoffTime.hour, localKickoffTime.minute)
                    PastFixtureStatView(statName = "Date", statValue = localKickoffTime.date.toString())
                    PastFixtureStatView(statName = "Kick Off Time", statValue = formattedTime)
                }
            }
        }
    }
}

/**
 * Displays a stat row with a name and value, separated by a divider.
 * This component is used for displaying fixture statistics.
 */
@Composable
fun PastFixtureStatView(statName: String, statValue: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = statName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = statValue,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(thickness = 1.dp)
    }
}