@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn


sealed class PlayerDetailsUiState {
    object Loading : PlayerDetailsUiState()
    data class Error(val message: String) : PlayerDetailsUiState()
    data class Success(val player: Player, val history: List<PlayerPastHistory>) : PlayerDetailsUiState()
}

open class PlayerDetailsViewModel(
    private val repository: FantasyPremierLeagueRepository,
) : ViewModel() {

    val playerId = MutableStateFlow<Int?>(null)

    fun setPlayer(id: Int) {
        playerId.value = id
    }

    val state: StateFlow<PlayerDetailsUiState> = playerId
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                try {
                    val player = repository.getPlayer(id)
                    val history = repository.getPlayerHistoryData(id)
                    emit(
                        PlayerDetailsUiState.Success(
                            player = player,
                            history = history,
                        )
                    )
                } catch(e: Exception) {
                    emit(PlayerDetailsUiState.Error(e.message ?: ""))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PlayerDetailsUiState.Loading)
}