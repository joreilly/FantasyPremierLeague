package dev.johnoreilly.fantasypremierleague.presentation

import androidx.lifecycle.*
import co.touchlab.kermit.Kermit
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import kotlinx.coroutines.flow.*


class FantasyPremierLeagueViewModel(
    private val repository: FantasyPremierLeagueRepository,
    private val logger: Kermit
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


    fun getFixture(fixtureId: Int?): GameFixture? {
        return fixturesList.value.find { it.id == fixtureId}
    }
}