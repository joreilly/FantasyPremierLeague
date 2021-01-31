package dev.johnoreilly.fantasypremierleague.ui.fixtures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import dev.johnoreilly.fantasypremierleague.ui.players.PlayersViewModel

@Composable
fun FixturesListView(playersViewModel: PlayersViewModel) {
    val fixtureListState = playersViewModel.fixtures.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fantasy Premier League") })
        },
        bodyContent = {
            Column {
                LazyColumn {
                    items(items = fixtureListState.value, itemContent = { fixture ->
                        Text(fixture.kickoff_time ?: "")
                    })
                }
            }
        }
    )
}