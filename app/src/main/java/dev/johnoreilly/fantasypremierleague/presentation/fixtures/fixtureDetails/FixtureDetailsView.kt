package dev.johnoreilly.fantasypremierleague.presentation.fixtures.fixtureDetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.data.model.GameSettingsDto
import dev.johnoreilly.fantasypremierleague.presentation.players.PlayersViewModel

@Composable
fun FixtureDetailsView(
    fixtureId: Int?,
    playersViewModel: PlayersViewModel,
    popBackStack: () -> Unit
) {
    val pastFixtureAsState = playersViewModel.getPastFixtureWithId(fixtureId).observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Fixture details")
                },
                navigationIcon = {
                    IconButton(onClick = { popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bodyContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "${pastFixtureAsState.value?.homeTeam} VS ${pastFixtureAsState.value?.awayTeam} ",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    )
}