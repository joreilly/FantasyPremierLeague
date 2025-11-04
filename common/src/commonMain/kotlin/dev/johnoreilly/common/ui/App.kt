package dev.johnoreilly.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.johnoreilly.common.ui.fixtures.FixturesListView
import dev.johnoreilly.common.ui.leagues.LeagueListView
import dev.johnoreilly.common.ui.players.PlayerListView
import dev.johnoreilly.common.ui.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.common.ui.settings.SettingsView
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
private sealed interface Route : NavKey

@Serializable
private sealed interface TopLevelRoute: Route {
    val icon: ImageVector
    val contentDescription: String
}

@Serializable
private data object PlayerList : TopLevelRoute {
    override val icon = Icons.Default.Person
    override val contentDescription = "Players"
}

@Serializable
private data class PlayerDetails(val playerId: Int) : Route

@Serializable
private data object FixtureList : TopLevelRoute {
    override val icon = Icons.Filled.DateRange
    override val contentDescription = "Fixtures"
}

@Serializable
private data object League : TopLevelRoute {
    override val icon = Icons.AutoMirrored.Filled.List
    override val contentDescription = "Leagues"
}

@Serializable
private data object Settings : Route

private val topLevelRoutes: List<TopLevelRoute> = listOf(PlayerList, FixtureList, League)


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun App() {
    MaterialTheme {
        val backStack = rememberNavBackStack<Route>(PlayerList)

        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
        val directive = remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                .copy(horizontalPartitionSpacerSize = 0.dp)
        }
        val listDetailStrategy = rememberListDetailSceneStrategy<Any>(directive = directive)

        Scaffold(
            bottomBar = { FantasyPremierLeagueBottomNavigation(topLevelRoutes, backStack) }
        ) { _ ->
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                sceneStrategy = listDetailStrategy,
                entryProvider = entryProvider {
                    entry<PlayerList>(
                        metadata = ListDetailSceneStrategy.listPane(
                            detailPlaceholder = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Choose a player from the list",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        )
                    ) {
                        PlayerListView(
                            onPlayerSelected = { player ->
                                backStack.add(PlayerDetails(player.id))
                            },
                            onShowSettings = { backStack.add(Settings) }
                        )
                    }
                    entry<PlayerDetails>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { key ->
                        val viewModel = koinViewModel<PlayerDetailsViewModel>()
                        viewModel.setPlayer(key.playerId)
                        PlayerDetailsView(
                            viewModel,
                            popBackStack = { backStack.removeLastOrNull() })
                    }
                    entry<FixtureList> { FixturesListView() }
                    entry<League> { LeagueListView() }
                    entry<Settings> { SettingsView { backStack.removeLastOrNull() } }
                },
            )
        }
    }
}


@Composable
private fun FantasyPremierLeagueBottomNavigation(
    topLevelRoutes: List<TopLevelRoute>,
    backStack: MutableList<Route>
) {
    var selectedType by remember { mutableStateOf<TopLevelRoute>(PlayerList) }
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = topLevelRoute.icon,
                        contentDescription = topLevelRoute.contentDescription
                    )
                },
                selected = topLevelRoute == selectedType,
                onClick = {
                    selectedType = topLevelRoute
                    backStack.add(topLevelRoute)
                }
            )
        }
    }
}
