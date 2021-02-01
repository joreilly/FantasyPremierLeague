package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
import dev.johnoreilly.fantasypremierleague.ui.Screen
import dev.johnoreilly.fantasypremierleague.ui.bottomNavigationItems
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

@Composable
fun MainLayout(playersViewModel: PlayersViewModel) {
    val navController = rememberNavController()

    FantasyPremierLeagueTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

                    bottomNavigationItems.forEach { bottomNavigationitem ->
                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    bottomNavigationitem.icon,
                                    contentDescription = bottomNavigationitem.iconContentDescription
                                )
                            },
                            selected = currentRoute == bottomNavigationitem.route,
                            onClick = {
                                navController.navigate(bottomNavigationitem.route) {
                                    popUpTo = navController.graph.startDestination
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        ) {
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
                        playersViewModel = playersViewModel,
                        popBackStack = { navController.popBackStack() }
                    )
                }
                composable(Screen.FixtureListScreen.title) {
                    FixturesListView(playersViewModel = playersViewModel)
                }
            }
        }
    }
}