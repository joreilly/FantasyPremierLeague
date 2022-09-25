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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray

@Composable
fun FixturesListView(
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel,
    onFixtureSelected: (fixtureId: Int) -> Unit,
) {
    val fixturesState = fantasyPremierLeagueViewModel.gameweekToFixtures.collectAsState()
    val currentGameweek: State<Int> = fantasyPremierLeagueViewModel.currentGameweek.collectAsState()
    val selectedGameweek = mutableStateOf(currentGameweek.value)
    val isLoading = fixturesState.value[currentGameweek.value] == null
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fixtures") })
        }) {
        Column {
            GameweekSelector(
                selectedGameweek = selectedGameweek.value,
                isDataLoading = isLoading,
                onGameweekChanged = {
                    if (it is GameweekChange.PastGameweek) selectedGameweek.value -= 1 else selectedGameweek.value += 1
                })
            LazyColumn {
                items(
                    items = fixturesState.value[selectedGameweek.value] ?: placeholderFixtureList,
                    itemContent = { fixture ->
                        FixtureView(
                            fixture = fixture,
                            onFixtureSelected = onFixtureSelected,
                            isDataLoading = isLoading
                        )
                    })
            }
        }
    }
}

@Composable
fun GameweekSelector(
    selectedGameweek: Int,
    onGameweekChanged: (gameweekChange: GameweekChange) -> Unit,
    isDataLoading: Boolean
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .placeholder(visible = isDataLoading, lowfidelitygray),
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

sealed class GameweekChange {
    object NextGameweek : GameweekChange()
    object PastGameweek : GameweekChange()
}

private val placeholderKickoffTime = kotlinx.datetime.LocalDateTime(2022, 9, 5, 13, 30, 0)
private val placeholderFixtureList = listOf(
    GameFixture(
        id = 1,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Liverpool",
        awayTeam = "Manchester United",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    ),
    GameFixture(
        id = 2,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Brighton",
        awayTeam = "Fulham",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    ),
    GameFixture(
        id = 3,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Chelsea",
        awayTeam = "Crystal Palace",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    ),
    GameFixture(
        id = 4,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Tottenham",
        awayTeam = "Wolves",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    ),
    GameFixture(
        id = 5,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Everton",
        awayTeam = "Southampton",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    )
)