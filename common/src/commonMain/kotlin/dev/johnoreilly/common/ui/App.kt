package dev.johnoreilly.common.ui

import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.johnoreilly.common.ui.fixtures.FixturesListView
import dev.johnoreilly.common.ui.leagues.LeagueListView
import dev.johnoreilly.common.ui.players.PlayerListView
import dev.johnoreilly.common.ui.players.playerDetails.PlayerDetailsView
import dev.johnoreilly.common.ui.settings.SettingsView
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel



private sealed interface TopLevelRoute {
    val icon: ImageVector
}
private data object PlayerList : TopLevelRoute { override val icon = Icons.Default.Person }
private data class PlayerDetails(val playerId: Int)
private data object FixtureList : TopLevelRoute { override val icon = Icons.Filled.DateRange }
private data object League : TopLevelRoute { override val icon = Icons.AutoMirrored.Filled.List }
private data object Settings
private val topLevelRoutes : List<TopLevelRoute> = listOf(PlayerList, FixtureList, League)


@Composable
fun App() {
    MaterialTheme {
        val backStack = mutableStateListOf<Any>(PlayerList)

        Scaffold(
            bottomBar = { FantasyPremierLeagueBottomNavigation(topLevelRoutes, backStack) }
        ){
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<PlayerList> {
                        PlayerListView(
                            onPlayerSelected = { player ->
                                backStack.add(PlayerDetails(player.id))
                            },
                            onShowSettings = {
                                backStack.add(Settings)
                            }
                        )
                    }
                    entry<PlayerDetails> { key ->
                        val viewModel = koinViewModel<PlayerDetailsViewModel>()
                        viewModel.setPlayer(key.playerId)
                        PlayerDetailsView(viewModel, popBackStack = { backStack.removeLastOrNull() })
                    }
                    entry<FixtureList> {
                        FixturesListView()
                    }
                    entry<League> {
                        LeagueListView()
                    }
                    entry<Settings> {
                        SettingsView { backStack.removeLastOrNull() }
                    }
                },
            )

        }
    }
}


@Composable
private fun FantasyPremierLeagueBottomNavigation(
    topLevelRoutes: List<TopLevelRoute>,
    backStack: SnapshotStateList<Any>
) {
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = topLevelRoute.icon,
                        contentDescription = null
                    )
                },
                selected = false, //currentRoute == bottomNavigationItem.route,
                onClick = {
                    backStack.add(topLevelRoute)
                }
            )
        }
    }
}
