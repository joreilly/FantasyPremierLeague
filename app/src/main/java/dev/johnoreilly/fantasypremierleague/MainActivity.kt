package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.Screen
import dev.johnoreilly.fantasypremierleague.presentation.bottomNavigationItems
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.FixturesListView
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.FixtureDetails.FixtureDetailsView
import dev.johnoreilly.fantasypremierleague.presentation.global.FantasyPremierLeagueTheme
import dev.johnoreilly.fantasypremierleague.presentation.leagues.LeagueListView
import dev.johnoreilly.fantasypremierleague.presentation.players.PlayerListView
import dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.fantasypremierleague.presentation.settings.SettingsView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private val fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FantasyPremierLeagueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout(fantasyPremierLeagueViewModel)
                }
            }
        }
    }
}

@Composable
fun MainLayout(viewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()

    val repository: FantasyPremierLeagueRepository = koinInject()

    Scaffold(
        bottomBar = { FantasyPremierLeagueBottomNavigation(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Column(Modifier.padding(it)) {
            NavHost(navController, startDestination = Screen.PlayerListScreen.title) {
                composable(Screen.PlayerListScreen.title) {
                    PlayerListView(
                        fantasyPremierLeagueViewModel = viewModel,
                        onPlayerSelected = { playerId ->
                            navController.navigate(Screen.PlayerDetailsScreen.title + "/${playerId}")
                        },
                        onShowSettings = {
                            navController.navigate(Screen.SettingsScreen.title)
                        }
                    )
                }
                composable(
                    Screen.PlayerDetailsScreen.title + "/{playerId}",
                    arguments = listOf(navArgument("playerId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val playerId: Int? = navBackStackEntry.arguments?.getInt("playerId")
                    playerId?.let {
                        val player = viewModel.getPlayer(playerId)
                        player?.let {
                            PlayerDetailsView(
                                repository,
                                player,
                                popBackStack = { navController.popBackStack() })
                        }
                    }
                }
                composable(Screen.FixtureListScreen.title) {
                    FixturesListView(
                        onFixtureSelected = { fixtureId ->
                            navController.navigate(Screen.FixtureDetailsScreen.title + "/${fixtureId}")
                        }
                    )
                }
                composable(
                    Screen.FixtureDetailsScreen.title + "/{fixtureId}",
                    arguments = listOf(navArgument("fixtureId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val fixtureId: Int? = navBackStackEntry.arguments?.getInt("fixtureId")
                    fixtureId?.let {
                        val fixture = viewModel.getFixture(fixtureId)
                        fixture?.let {
                            FixtureDetailsView(
                                fixture,
                                popBackStack = { navController.popBackStack() })
                        }
                    }
                }
                composable(Screen.LeagueStandingsListScreen.title) {
                    LeagueListView(viewModel)
                }
                composable(Screen.SettingsScreen.title) {
                    SettingsView(viewModel, popBackStack = { navController.popBackStack() })
                }
            }
        }
    }
}


@Composable
private fun FantasyPremierLeagueBottomNavigation(navController: NavHostController) {

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavigationItems.forEach { bottomNavigationItem ->

            NavigationBarItem(
                icon = {
                    Icon(
                        bottomNavigationItem.icon,
                        contentDescription = bottomNavigationItem.iconContentDescription
                    )
                },
                selected = currentRoute == bottomNavigationItem.route,
                onClick = {
                    navController.navigate(bottomNavigationItem.route) {
                        popUpTo(navController.graph.id)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
