package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel

@Composable
fun FixturesListView(
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel,
    onFixtureSelected: (fixtureId: Int) -> Unit
) {
    val fixturesState = fantasyPremierLeagueViewModel.gameweekToFixtures.collectAsState()
    val gameweek = remember { mutableStateOf(fantasyPremierLeagueViewModel.currentGameweek) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fixtures") })
        }) {
        Column {
            GameweekSelector(selectedGameweek = gameweek.value, onGameweekChanged = {
                if (it is GameweekChange.PastGameweek) gameweek.value -= 1 else gameweek.value += 1
            })
            LazyColumn {
                items(
                    items = fixturesState.value[gameweek.value] ?: emptyList(),
                    itemContent = { fixture ->
                        FixtureView(
                            fixture = fixture,
                            onFixtureSelected = onFixtureSelected
                        )
                    })
            }
        }
    }
}

@Composable
fun GameweekSelector(
    selectedGameweek: Int,
    onGameweekChanged: (gameweekChange: GameweekChange) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (selectedGameweek > 1) {
            IconButton(
                modifier = Modifier
                    .width(30.dp)
                    .background(color = Color.LightGray),
                onClick = { onGameweekChanged(GameweekChange.PastGameweek) }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back arrow")
            }
        } else {
            Spacer(modifier = Modifier.width(30.dp))
        }

        Text(
            text = "Gameweek $selectedGameweek",
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp
        )

        if (selectedGameweek < 38) {
            IconButton(
                modifier = Modifier
                    .width(30.dp)
                    .background(color = Color.LightGray),
                onClick = { onGameweekChanged(GameweekChange.NextGameweek) }) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Forward arrow")
            }
        } else {
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}

sealed class GameweekChange() {
    object NextGameweek : GameweekChange()
    object PastGameweek : GameweekChange()
}