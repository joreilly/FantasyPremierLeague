package dev.johnoreilly.fantasypremierleague.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import dev.chrisbanes.accompanist.coil.CoilImage
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
    object PlayerDetailsScreen : Screen("PlayerDetailsScreen")
    object FixtureListScreen : Screen("FixtureListScreen")
}

@Composable
fun MainLayout(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()

    FantasyPremierLeagueTheme {
        NavHost(navController, startDestination = Screen.PlayerListScreen.title) {
            composable(Screen.PlayerListScreen.title) {
                PlayerListView(
                    fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel,
                    navController
                )
            }
            composable(
                "${Screen.PlayerDetailsScreen.title}/{playerId}",
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val playerId: Int? = navBackStackEntry.arguments?.getInt("playerId")
                val player = fantasyPremierLeagueViewModel.players.value?.first { it.id == playerId }

                // TODO error handling for invalid playerId edge case or when first throws an exception
                PlayerDetailsView(
                    player = player!!,
                    fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel
                )
            }
            composable(Screen.FixtureListScreen.title) {
                FixtureList(fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel)
            }
        }
    }
}

@Composable
fun PlayerListView(
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel,
    navController: NavController
) {
    val playerList = fantasyPremierLeagueViewModel.players.observeAsState()
    val playerSearchQuery = fantasyPremierLeagueViewModel.query

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Fantasy Premier League")
                }
            )
        },
        bodyContent = {
            Column {
                TextField(
                    singleLine = true,
                    value = playerSearchQuery.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = {
                        Text(text = "Search")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    onImeActionPerformed = { action, softKeyboardController ->
                        if (action == ImeAction.Search) {
                            fantasyPremierLeagueViewModel.onPlayerSearchQueryChange(
                                playerSearchQuery.value
                            )
                            softKeyboardController?.hideSoftwareKeyboard()
                        }
                    },
                    onValueChange = {
                        playerSearchQuery.value = it
                        fantasyPremierLeagueViewModel.onPlayerSearchQueryChange(it)
                    }
                )

                playerList.value?.let {
                    LazyColumn {
                        items(items = it, itemContent = { player ->
                            PlayerView(player, navController)
                        })
                    }
                }
            }
        }
    )
}

@Composable
fun PlayerView(
    player: Player,
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("${Screen.PlayerDetailsScreen.title}/${player.id}")
            }
    ) {
        CoilImage(
            data = player.photoUrl,
            modifier = Modifier.preferredSize(60.dp),
            contentDescription = player.name
        )
        Spacer(modifier = Modifier.preferredSize(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(player.name, style = typography.h6)
            Text(player.team, style = typography.subtitle1, color = Color.DarkGray)
        }
        Text(player.points.toString(), style = typography.h6)
    }
}

@Composable
fun PlayerDetailsView(
    player: Player,
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .preferredHeight(150.dp)
                .fillMaxWidth()
        ) {
            CoilImage(
                data = player.photoUrl,
                modifier = Modifier.preferredSize(150.dp),
                contentDescription = player.name
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .preferredHeight(100.dp)
                    .fillMaxWidth()
            ) {
                Text(player.name, style = typography.h6)
                Text(player.team, style = typography.subtitle1, color = Color.DarkGray)
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            Text(text = "POINTS:", style = typography.subtitle1)
            Text(
                player.points.toString(),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FixtureList(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel) {
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