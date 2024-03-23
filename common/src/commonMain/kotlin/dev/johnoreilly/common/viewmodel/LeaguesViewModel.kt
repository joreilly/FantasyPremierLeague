package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.model.EventStatusDto
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class LeaguesViewModel : ViewModel(), KoinComponent {
    private val repository: FantasyPremierLeagueRepository by inject()

    var leagueStandings = repository.leagues.map { leagues ->
        leagues.map { leagueId ->
            repository.getLeagueStandings(leagueId.trim().toInt())
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _isRefreshing = MutableStateFlow(false)

    fun updateLeagues(leagues: List<String>) {
        repository.updateLeagues(leagues)
    }

    suspend fun getEventStatus(): List<EventStatusDto> {
        return repository.getEventStatus().status
    }
}