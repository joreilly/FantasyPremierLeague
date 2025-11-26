package dev.johnoreilly.common.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import dev.johnoreilly.common.ui.fixtures.FixturesListView
import dev.johnoreilly.common.ui.leagues.LeagueListView
import dev.johnoreilly.common.ui.players.PlayerListView
import dev.johnoreilly.common.ui.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.common.ui.settings.SettingsView
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Serializable
private sealed interface Route

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


@Composable
fun App() {
    MaterialTheme {
        val backStack: MutableList<Route> =
            rememberSerializable(serializer = SnapshotStateListSerializer()) {
                mutableStateListOf(PlayerList)
            }
        val listDetailStrategy = rememberListDetailSceneStrategy<Route>()

        Scaffold(
            bottomBar = { FantasyPremierLeagueBottomNavigation(topLevelRoutes, backStack) }
        ) { _ ->
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                sceneStrategy = listDetailStrategy,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<PlayerList>(
                        metadata = ListDetailScene.listPane()
                    ) {
                        PlayerListView(
                            onPlayerSelected = { player ->
                                backStack.add(PlayerDetails(player.id))
                            },
                            onShowSettings = { backStack.add(Settings) }
                        )
                    }
                    entry<PlayerDetails>(
                        metadata = ListDetailScene.detailPane()
                    ) { key ->
                        val viewModel = koinViewModel<PlayerDetailsViewModel>(
                            parameters = { parametersOf(key.playerId) }
                        )
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
