package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
import dev.johnoreilly.fantasypremierleague.ui.global.FantasyPremierLeagueTheme
import dev.johnoreilly.fantasypremierleague.ui.players.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.ui.fixtures.FixturesListView
import dev.johnoreilly.fantasypremierleague.ui.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.fantasypremierleague.ui.players.PlayerListView
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
                val player = fantasyPremierLeagueViewModel.players.value?.first { it.id == playerId }

                // TODO error handling for invalid playerId edge case or when first throws an exception
                PlayerDetailsView(
                    player = player!!,
                    fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel
                )
            }
            composable(Screen.FixtureListScreen.title) {
                FixturesListView(fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel)
            }
        }
    }
}