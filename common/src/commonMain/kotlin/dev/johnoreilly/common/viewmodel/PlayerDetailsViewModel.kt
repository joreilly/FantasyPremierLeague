package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn


sealed class PlayerDetailsUiState {
    object Loading : PlayerDetailsUiState()
    data class Error(val message: String) : PlayerDetailsUiState()
    data class Success(val player: Player, val history: List<PlayerPastHistory>) : PlayerDetailsUiState()
}

open class PlayerDetailsViewModel(
    private val playerId: Int,
    private val repository: FantasyPremierLeagueRepository,
) : ViewModel() {

    val state =
        combine(
            flow { emit(repository.getPlayer(playerId)) },
            flow { emit(repository.getPlayerHistoryData(playerId)) }
        ) { player, history ->
            PlayerDetailsUiState.Success(
                player = player,
                history = history,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}