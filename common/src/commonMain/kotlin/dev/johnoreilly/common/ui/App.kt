package dev.johnoreilly.common.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import dev.johnoreilly.common.ui.agent.AgentScreen
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

// Full-screen player details reached from the assistant (not a list-detail pane),
// so it opens as a dedicated screen you can navigate back from.
@Serializable
private data class AssistantPlayerDetails(val playerId: Int) : Route

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
private data object Assistant : TopLevelRoute {
    override val icon = Icons.Filled.Face
    override val contentDescription = "Assistant"
}

@Serializable
private data object Settings : Route

private val topLevelRoutes: List<TopLevelRoute> = listOf(PlayerList, FixtureList, League, Assistant)


/**
 * Pops unless we're already at the root. NavDisplay requires a non-empty backstack and throws if it
 * ever sees one, so nothing may remove the last entry.
 *
 * A bare removeLastOrNull() is one pop away from crashing whenever a dismiss callback fires more
 * than once - a double tap, a key repeat, or NavDisplay itself, which calls onBack once per entry
 * it needs dropped rather than once per back event.
 */
private fun MutableList<Route>.popBackStack() {
    if (size > 1) removeLastOrNull()
}

/**
 * Pushes unless that route is already on top, so a double tap - or a dismiss callback that fires
 * twice - can't stack the same destination on itself.
 */
private fun MutableList<Route>.push(route: Route) {
    if (lastOrNull() != route) add(route)
}

/**
 * Switches to a top-level destination, making it the only root.
 *
 * A bottom bar is a set of parallel sections rather than a history, so tabs replace the stack
 * instead of piling onto it - otherwise Players -> Fixtures -> Leagues leaves three entries and
 * back walks through the tabs you visited rather than leaving the app. Re-selecting the current
 * tab pops back to its root, the conventional "return to the top of this section" gesture.
 */
private fun MutableList<Route>.switchToTopLevelRoute(route: TopLevelRoute) {
    if (size == 1 && lastOrNull() == route) return
    add(route)
    // Drop everything beneath the new root only after adding it, so the list is never momentarily
    // empty - the one state NavDisplay refuses to render.
    while (size > 1) removeAt(0)
}


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
        ) { padding ->
            NavDisplay(
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                backStack = backStack,
                onBack = { backStack.popBackStack() },
                sceneStrategies = listOf(listDetailStrategy),
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
                                backStack.push(PlayerDetails(player.id))
                            },
                            onShowSettings = { backStack.push(Settings) }
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
                            popBackStack = { backStack.popBackStack() })
                    }
                    entry<FixtureList> { FixturesListView() }
                    entry<League> { LeagueListView() }
                    entry<Assistant> {
                        AgentScreen(onPlayerSelected = { playerId -> backStack.push(AssistantPlayerDetails(playerId)) })
                    }
                    entry<AssistantPlayerDetails> { key ->
                        val viewModel = koinViewModel<PlayerDetailsViewModel>(
                            parameters = { parametersOf(key.playerId) }
                        )
                        PlayerDetailsView(
                            viewModel,
                            popBackStack = { backStack.popBackStack() })
                    }
                    entry<Settings> { SettingsView { backStack.popBackStack() } }
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
    // Derived rather than held separately: popping a detail route used to leave the highlighted
    // tab pointing at wherever the user last tapped, out of step with the screen on show.
    val selectedRoute = backStack.filterIsInstance<TopLevelRoute>().lastOrNull() ?: PlayerList
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = topLevelRoute.icon,
                        contentDescription = topLevelRoute.contentDescription
                    )
                },
                selected = topLevelRoute == selectedRoute,
                onClick = { backStack.switchToTopLevelRoute(topLevelRoute) }
            )
        }
    }
}
