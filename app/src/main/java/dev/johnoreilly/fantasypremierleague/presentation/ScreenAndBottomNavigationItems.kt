package dev.johnoreilly.fantasypremierleague.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String) {
    object PlayerListScreen : Screen("PlayerListScreen")
    object PlayerDetailsScreen : Screen("PlayerDetailsScreen")
    object FixtureListScreen : Screen("FixtureListScreen")
    object FixtureDetailsScreen : Screen("FixtureDetailsScreen")
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
)