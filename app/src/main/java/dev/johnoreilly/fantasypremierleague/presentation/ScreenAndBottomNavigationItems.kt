package dev.johnoreilly.fantasypremierleague.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String) {
    data object PlayerListScreen : Screen("PlayerListScreen")
    data object PlayerDetailsScreen : Screen("PlayerDetailsScreen")
    data object FixtureListScreen : Screen("FixtureListScreen")
    data object FixtureDetailsScreen : Screen("FixtureDetailsScreen")
    data object LeagueStandingsListScreen : Screen("LeagueStandingsListScreen")
    data object SettingsScreen : Screen("SettingsScreen")
}

data class BottomNavigationitem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationitem(
        Screen.PlayerListScreen.title,
        Icons.Default.Person,
        "Player"
    ),
    BottomNavigationitem(
        Screen.FixtureListScreen.title,
        Icons.Filled.DateRange,
        "Fixtures"
    ),
    BottomNavigationitem(
        Screen.LeagueStandingsListScreen.title,
        Icons.Filled.List,
        "League"
    )
)