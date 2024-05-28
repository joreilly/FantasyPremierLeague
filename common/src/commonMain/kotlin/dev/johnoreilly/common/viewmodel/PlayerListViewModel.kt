package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
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


sealed class PlayerListUIState {
    object Loading : PlayerListUIState()
    data class Error(val message: String) : PlayerListUIState()
    data class Success(val result: List<Player>) : PlayerListUIState()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
open class PlayerListViewModel : ViewModel(), KoinComponent {
    private val repository: FantasyPremierLeagueRepository by inject()

    val allPlayers = repository.getPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchQuery = MutableStateFlow("")
    val playerListUIState: StateFlow<PlayerListUIState> =
        searchQuery.debounce(250).flatMapLatest { searchQuery ->
            allPlayers.mapLatest { playerList ->
                if (playerList.isNotEmpty()) {
                    val players = playerList
                        .filter { it.name.contains(searchQuery, ignoreCase = true) }
                        .sortedByDescending { it.points }
                    PlayerListUIState.Success(players)
                } else {
                    PlayerListUIState.Loading
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, PlayerListUIState.Loading)

    fun onPlayerSearchQueryChange(query: String) {
        searchQuery.value = query
    }
}