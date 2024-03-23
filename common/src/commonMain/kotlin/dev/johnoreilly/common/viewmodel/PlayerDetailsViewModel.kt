package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class PlayerDetailsViewModel : ViewModel(), KoinComponent {
    private val repository: FantasyPremierLeagueRepository by inject()


    suspend fun getPlayerHistory(playerId: Int): List<PlayerPastHistory> {
        return repository.getPlayerHistoryData(playerId)
    }

}