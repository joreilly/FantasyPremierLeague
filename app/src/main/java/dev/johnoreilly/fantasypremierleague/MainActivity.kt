package dev.johnoreilly.fantasypremierleague

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.compiler.plugins.kotlin.ComposeFqNames.remember
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.window.TwoPane
import androidx.compose.material.window.TwoPaneState
import androidx.compose.material.window.rememberTwoPaneState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import dev.johnoreilly.fantasypremierleague.presentation.Screen
import dev.johnoreilly.fantasypremierleague.presentation.bottomNavigationItems
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.FixturesListView
import dev.johnoreilly.fantasypremierleague.presentation.fixtures.fixtureDetails.FixtureDetailsView
import dev.johnoreilly.fantasypremierleague.presentation.global.FantasyPremierLeagueTheme
import dev.johnoreilly.fantasypremierleague.presentation.players.PlayerListView
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails.PlayerDetailsView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel by viewModel()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass

            val twoPaneState = rememberTwoPaneState(
                startFractionSplit = 1f / 3f
            )

            FantasyPremierLeagueTheme {
                MainLayout(twoPaneState, widthSizeClass, fantasyPremierLeagueViewModel)
            }
        }
    }
}

@Composable
fun MainLayout(
    twoPaneState: TwoPaneState,
    widthSizeClass: WindowWidthSizeClass,
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel
) {
    val navController = rememberNavController()


    var playerId by rememberSaveable { mutableStateOf(0) }

    val start = remember {
        movableContentOf { modifier: Modifier ->
            PlayerListView(
                fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel,
                onPlayerSelected = { id ->
                    playerId = id
                }
            )
        }
    }

    val end = remember {
        movableContentOf { modifier: Modifier ->
            val player = fantasyPremierLeagueViewModel.getPlayer(playerId)
            player?.let {
                PlayerDetailsView(player, popBackStack = { navController.popBackStack() })
            }
        }
    }

    val isCompactScreen = widthSizeClass == WindowWidthSizeClass.Compact
    if (isCompactScreen) {
        start(Modifier.padding(8.dp))
    } else {
        TwoPane(
            start = {  start(Modifier.padding(end = 8.dp)) },
            end = { end(Modifier.padding(start = 8.dp)) },
            state = twoPaneState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        )
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainCompactLayout(fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()


    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                                popUpTo(navController.graph.id)
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
                playerId?.let {
                    val player = fantasyPremierLeagueViewModel.getPlayer(playerId)
                    player?.let {
                        PlayerDetailsView(player, popBackStack = { navController.popBackStack() })
                    }
                }
            }
            composable(Screen.FixtureListScreen.title) {
                FixturesListView(
                    fantasyPremierLeagueViewModel = fantasyPremierLeagueViewModel,
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
                    val fixture = fantasyPremierLeagueViewModel.getFixture(fixtureId)
                    fixture?.let {
                        FixtureDetailsView(fixture, popBackStack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

}


@Composable
private fun getScreenType(isExpandedScreen: Boolean): Screen = when (isExpandedScreen) {
    false -> Screen.PlayerListScreen
    true -> Screen.PlayerListWithDetailsScreen
}