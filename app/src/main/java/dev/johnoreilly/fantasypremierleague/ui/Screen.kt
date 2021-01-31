package dev.johnoreilly.fantasypremierleague.ui

sealed class Screen(val title: String) {
    object PlayerListScreen : Screen("PlayerListScreen")
    object PlayerDetailsScreen : Screen("PlayerDetailsScreen")
    object FixtureListScreen : Screen("FixtureListScreen")
}
