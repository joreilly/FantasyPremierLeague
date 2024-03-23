package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class FixturesViewModel : ViewModel(), KoinComponent {
    private val repository: FantasyPremierLeagueRepository by inject()

    val gameWeekFixtures = repository.gameWeekFixtures

    val currentGameweek: StateFlow<Int>
        get() = repository.currentGameweek

}