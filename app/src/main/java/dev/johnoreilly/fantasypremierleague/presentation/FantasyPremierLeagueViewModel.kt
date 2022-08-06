package dev.johnoreilly.fantasypremierleague.presentation

import androidx.lifecycle.*
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import kotlinx.coroutines.flow.*


class FantasyPremierLeagueViewModel(
    private val repository: FantasyPremierLeagueRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val playerList: StateFlow<List<Player>> = searchQuery.debounce(250).flatMapLatest { searchQuery ->
        repository.playerList.mapLatest { playerList ->
            playerList
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .sortedByDescending { it.points }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val fixturesList = repository.fixtureList

    fun onPlayerSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun getPlayer(playerId: Int): Player? {
        return playerList.value.find { it.id == playerId}
    }

    suspend fun getPlayerHistory(playerId: Int): List<PlayerPastHistory> {
        return repository.getPlayerHistoryData(playerId)
    }

    suspend fun getLeagueStandings(leagueId: Int): LeagueStandingsDto {
        return repository.getLeagueStandings(leagueId)
    }

    fun getFixture(fixtureId: Int?): GameFixture? {
        return fixturesList.value.find { it.id == fixtureId}
    }
}