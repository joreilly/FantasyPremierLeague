package dev.johnoreilly.fantasypremierleague.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.johnoreilly.common.model.Element
import dev.johnoreilly.common.remote.Fixture
import dev.johnoreilly.common.repository.Player
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainLayout(fantasyPremierLeagueViewModel)
        }
    }

}


sealed class Screen(val title: String) {
    object PlayerListScreen : Screen("PlayerListScreen")
    object FixtureListScreen : Screen("FixtureListScreen")
}

@Composable
fun MainLayout(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()

    FantasyPremierLeagueTheme {
        NavHost(navController, startDestination = Screen.PlayerListScreen.title) {
            composable(Screen.PlayerListScreen.title) {
                PlayerListView(fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel,
                    playerSelected = {
                    }
                )
            }
            composable(Screen.FixtureListScreen.title) {
                FixtureList(fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel,
                    fixtureSelected = {
                        //navController.navigate(Screen.PersonDetailsDetails.title + "/${it.name}")
                    }
                )
            }
        }
    }
}

@Composable
fun FixtureList(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel, fixtureSelected : (fixture : Fixture) -> Unit) {
    val fixtureListState = fantasyPremierLeagueViewModel.fixtures.observeAsState(emptyList())


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


@Composable
fun PlayerListView(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel, playerSelected : (player : Element) -> Unit) {
    val playerList = fantasyPremierLeagueViewModel.players.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fantasy Premier League") })
        },
        bodyContent = {
            Column {
                playerList.value?.let {
                    LazyColumn {
                        items(items = it, itemContent = { player ->
                            PlayerView(player)
                        })
                    }
                }
            }
        }
    )
}

@Composable
fun PlayerView(player: Player) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp).fillMaxWidth()) {

        CoilImage(data = player.photoUrl, modifier = Modifier.preferredSize(60.dp), contentDescription = player.name)
        Spacer(modifier = Modifier.preferredSize(12.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(player.name, style = typography.h6)
            Text(player.team, style = typography.subtitle1, color = Color.DarkGray)
        }

        Text(player.points.toString(), style = typography.h6)
    }

}