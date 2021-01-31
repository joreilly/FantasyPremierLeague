package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
import dev.johnoreilly.fantasypremierleague.ui.global.FantasyPremierLeagueTheme
import dev.johnoreilly.fantasypremierleague.ui.players.PlayersViewModel
import dev.johnoreilly.fantasypremierleague.ui.fixtures.FixturesListView
import dev.johnoreilly.fantasypremierleague.ui.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.fantasypremierleague.ui.players.PlayerListView
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val playersViewModel: PlayersViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainLayout(playersViewModel)
        }
    }
}

sealed class Screen(val title: String) {
    object PlayerListScreen : Screen("PlayerListScreen")
    object PlayerDetailsScreen : Screen("PlayerDetailsScreen")
    object FixtureListScreen : Screen("FixtureListScreen")
}

@Composable
fun MainLayout(playersViewModel: PlayersViewModel) {
    val navController = rememberNavController()

    FantasyPremierLeagueTheme {
        NavHost(navController, startDestination = Screen.PlayerListScreen.title) {
            composable(Screen.PlayerListScreen.title) {
                PlayerListView(
                    playersViewModel = playersViewModel,
                    onPlayerSelected = { playerId ->
                        navController.navigate(Screen.PlayerDetailsScreen.title + "/${playerId}")
                    }
                )
            }
            composable(
                Screen.PlayerDetailsScreen.title + "/{playerId}",
                arguments = listOf(navArgument("playerId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val playerId: Int? = navBackStackEntry.arguments?.getInt("playerId")
                val player = playersViewModel.players.value?.first { it.id == playerId }

                // TODO error handling for invalid playerId edge case or when first throws an exception
                PlayerDetailsView(
                    player = player!!,
                    playersViewModel = playersViewModel
                )
            }
            composable(Screen.FixtureListScreen.title) {
                FixturesListView(playersViewModel = playersViewModel)
            }
        }
    }
}