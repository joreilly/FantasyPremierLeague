package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import dev.johnoreilly.fantasypremierleague.presentation.players.PlayersViewModel

@Composable
fun FixturesListView(
    playersViewModel: PlayersViewModel,
    onFixtureSelected: (fixtureId: Int) -> Unit
) {
    val pastFixturesState = playersViewModel.pastFixtures.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fantasy Premier League") })
        }) {
            Column {
                LazyColumn {
                    items(items = pastFixturesState.value, itemContent = { fixture ->
                        FixtureView(
                            fixture = fixture,
                            onFixtureSelected = onFixtureSelected
                        )
                    })
                }
            }
        }
}