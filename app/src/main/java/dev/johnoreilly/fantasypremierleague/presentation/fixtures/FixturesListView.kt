package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray
import dev.johnoreilly.fantasypremierleague.presentation.global.maroon200

@Composable
fun FixturesListView(
    onFixtureSelected: (fixtureId: Int) -> Unit,
) {
    val fixturesViewModel: FixturesViewModel = viewModel()

    val fixturesState = fixturesViewModel.gameweekToFixtures.collectAsState()
    val currentGameweek: State<Int> = fixturesViewModel.currentGameweek.collectAsState()
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
                val fixtureItems: List<GameFixture> = if(isLoading) placeholderFixtureList
                    else fixturesState.value[selectedGameweek.value] ?: emptyList()
                items(
                    items = fixtureItems,
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
                    .width(30.dp),
                onClick = { onGameweekChanged(GameweekChange.PastGameweek) }) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Back arrow",
                    tint = maroon200
                )
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
                    .width(30.dp),
                onClick = { onGameweekChanged(GameweekChange.NextGameweek) }) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Forward arrow",
                    tint = maroon200
                )
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
        id = 1,
        localKickoffTime = placeholderKickoffTime,
        homeTeam = "Liverpool",
        awayTeam = "Manchester United",
        homeTeamPhotoUrl = "",
        awayTeamPhotoUrl = "",
        homeTeamScore = null,
        awayTeamScore = null,
        event = 0
    )
)