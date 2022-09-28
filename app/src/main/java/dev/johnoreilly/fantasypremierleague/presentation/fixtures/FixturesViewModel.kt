package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.lifecycle.ViewModel
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.java.KoinJavaComponent.inject

class FixturesViewModel: ViewModel() {

    private val repository: FantasyPremierLeagueRepository by inject(FantasyPremierLeagueRepository::class.java)

    val gameweekToFixtures = repository.gameweekToFixtures

    val currentGameweek: StateFlow<Int>
        get() = repository.currentGameweek
}