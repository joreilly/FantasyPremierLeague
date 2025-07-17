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
import org.koin.core.component.KoinComponent

open class PlayerDetailsViewModel(
    private val playerId: Int,
    private val repository: FantasyPremierLeagueRepository,
) : ViewModel(), KoinComponent {

    data class PlayerWithHistory(
        val player: Player,
        val history: List<PlayerPastHistory>
    )

    val state =
        combine(
            flow { emit(repository.getPlayer(playerId)) },
            flow { emit(repository.getPlayerHistoryData(playerId)) }
        ) { player, history ->
            PlayerWithHistory(
                player = player,
                history = history,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}