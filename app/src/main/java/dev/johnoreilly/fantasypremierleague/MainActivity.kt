package dev.johnoreilly.fantasypremierleague

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.rememberListDetailPaneScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainLayout(viewModel: FantasyPremierLeagueViewModel) {
    val navController = rememberNavController()

    val repository: FantasyPremierLeagueRepository = koinInject()

    val state = rememberListDetailPaneScaffoldState()

/*
    var selectedPlayer: Int? by rememberSaveable { mutableStateOf(null) }

    ListDetailPaneScaffold(
        scaffoldState = state,
        listPane = {

            PlayerListView(
                fantasyPremierLeagueViewModel = viewModel,
                onPlayerSelected = { playerId ->
                    selectedPlayer = playerId
                    state.navigateTo(ListDetailPaneScaffoldRole.Detail)
                },
                onShowSettings = {
                    navController.navigate(Screen.SettingsScreen.title)
                }
            )



//            MyList(
//                onItemClick = { id ->
//                    // Set current item
//                    selectedItem = id
//                    // Display the details pane
//                    state.navigateTo(ListDetailPaneScaffoldRole.Detail)
//                }
//            )
        },
        detailPane = {
            // Show the details pane content if selected item is available
            selectedPlayer?.let { selectedPlayer ->
                val player = viewModel.getPlayer(selectedPlayer)
                player?.let {
                    PlayerDetailsView(
                        repository,
                        player,
                        popBackStack = { navController.popBackStack() })
                }
            }
        },
    )

 */


    // Currently selected item
    var selectedItem: MyItem? by rememberSaveable { mutableStateOf(null) }

    ListDetailPaneScaffold(
        scaffoldState = state,
        listPane = {
            MyList(
                onItemClick = { id ->
                    // Set current item
                    selectedItem = id
                    // Display the details pane
                    state.navigateTo(ListDetailPaneScaffoldRole.Detail)
                },
            )
        },
        detailPane = {
            // Show the details pane content if selected item is available
            selectedItem?.let { item ->
                MyDetails(item)
            }
        },
    )


/*
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

 */
}

@Composable
fun MyList(
    onItemClick: (MyItem) -> Unit,
) {
    Card {
        LazyColumn {
            shortStrings.forEachIndexed { id, string ->
                item {
                    ListItem(
                        modifier = Modifier
                            .background(Color.Magenta)
                            .clickable {
                                onItemClick(MyItem(id))
                            },
                        headlineContent = {
                            Text(
                                text = string,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MyDetails(item: MyItem) {
    val text = shortStrings[item.id]
    Card {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Details page for $text",
                fontSize = 24.sp,
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "TODO: Add great details here"
            )
        }
    }
}

class MyItem(val id: Int)

val shortStrings = listOf(
    "Android",
    "Petit four",
    "Cupcake",
    "Donut",
    "Eclair",
    "Froyo",
    "Gingerbread",
    "Honeycomb",
    "Ice cream sandwich",
    "Jelly bean",
    "Kitkat",
    "Lollipop",
    "Marshmallow",
    "Nougat",
    "Oreo",
    "Pie",
)

@Composable
private fun FantasyPremierLeagueBottomNavigation(navController: NavHostController) {

    NavigationBar(modifier = Modifier.navigationBarsPadding()) {
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
