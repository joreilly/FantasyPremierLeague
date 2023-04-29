package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.lifecycle.ViewModel
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import kotlinx.coroutines.flow.StateFlow


class FixturesViewModel(private val repository: FantasyPremierLeagueRepository): ViewModel() {
    val gameweekToFixtures = repository.gameweekToFixtures

    val currentGameweek: StateFlow<Int>
        get() = repository.currentGameweek
}