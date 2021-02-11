package dev.johnoreilly.fantasypremierleague.presentation.fixtures.fixtureDetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
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
                Text(
                    text = "Team vs Team",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "INFO",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}