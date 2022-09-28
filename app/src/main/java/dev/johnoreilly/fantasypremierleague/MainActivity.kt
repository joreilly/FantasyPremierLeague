package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.Screen
import dev.johnoreilly.fantasypremierleague.presentation.bottomNavigationItems
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.FixturesListView
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.fixtureDetails.FixtureDetailsView
import dev.johnoreilly.fantasypremierleague.presentation.global.FantasyPremierLeagueTheme
import dev.johnoreilly.fantasypremierleague.presentation.leagues.LeagueListView
import dev.johnoreilly.fantasypremierleague.presentation.players.PlayerListView
import dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails.PlayerDetailsView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FantasyPremierLeagueTheme {
                MainLayout(fantasyPremierLeagueViewModel)
            }
        }
    }
}

@Composable
fun MainLayout(viewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val leagues by viewModel.leagues.collectAsState()

    coroutineScope.launch {
        // TEMP to set a particular league until settings screen added
        //viewModel.updateLeagues(listOf(""))
    }

    Scaffold(bottomBar = { FantasyPremierLeagueBottomNavigation(navController, leagues) }) {

        NavHost(navController, startDestination = Screen.PlayerListScreen.title, modifier = Modifier.padding(it)) {
            composable(Screen.PlayerListScreen.title) {
                PlayerListView(
                    fantasyPremierLeagueViewModel = viewModel,
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
                playerId?.let {
                    val player = viewModel.getPlayer(playerId)
                    player?.let {
                        PlayerDetailsView(viewModel, player, popBackStack = { navController.popBackStack() })
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
                        FixtureDetailsView(fixture, popBackStack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.LeagueStandingsListScreen.title) {
                LeagueListView(
                    viewModel = viewModel
                )
            }

        }
    }
}


@Composable
private fun FantasyPremierLeagueBottomNavigation(navController: NavHostController, leagues: List<String>) {

    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavigationItems.forEach { bottomNavigationitem ->

            val skipItem =  bottomNavigationitem.route == Screen.LeagueStandingsListScreen.title
                    && leagues.isEmpty()

            if (!skipItem) {
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
                            popUpTo(navController.graph.id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

}
