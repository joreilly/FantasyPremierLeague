package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.Player
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
open class PlayerListViewModel : ViewModel(), KoinComponent {
    private val repository: FantasyPremierLeagueRepository by inject()

    val allPlayers = repository.playerList

    val searchQuery = MutableStateFlow("")
    val playerList: StateFlow<List<Player>> =
        searchQuery.debounce(250).flatMapLatest { searchQuery ->
            allPlayers.mapLatest { playerList ->
                playerList
                    .filter { it.name.contains(searchQuery, ignoreCase = true) }
                    .sortedByDescending { it.points }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onPlayerSearchQueryChange(query: String) {
        searchQuery.value = query
    }

}