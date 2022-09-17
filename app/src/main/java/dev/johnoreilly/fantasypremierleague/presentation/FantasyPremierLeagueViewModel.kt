package dev.johnoreilly.fantasypremierleague.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.model.LeagueResultDto
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class FantasyPremierLeagueViewModel(
    private val repository: FantasyPremierLeagueRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val playerList: StateFlow<List<Player>> =
        searchQuery.debounce(250).flatMapLatest { searchQuery ->
            repository.playerList.mapLatest { playerList ->
                playerList
                    .filter { it.name.contains(searchQuery, ignoreCase = true) }
                    .sortedByDescending { it.points }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val fixturesList = repository.fixtureList

    val gameweekToFixtures = repository.gameweekToFixtures

    val leagues: StateFlow<List<String>> = repository.leagues
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var leagueStandings = MutableStateFlow((emptyList<LeagueResultDto>()))
    var leagueName = MutableStateFlow("")

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    
    fun onPlayerSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun getPlayer(playerId: Int): Player? {
        return playerList.value.find { it.id == playerId}
    }

    suspend fun getPlayerHistory(playerId: Int): List<PlayerPastHistory> {
        return repository.getPlayerHistoryData(playerId)
    }

    fun getLeagueStandings() {
        viewModelScope.launch {
            if (leagues.value.isNotEmpty()) {
                _isRefreshing.emit(true)
                val leagueId = leagues.value[0]
                val result = repository.getLeagueStandings(leagueId.toInt())
                result.let {
                    leagueName.value = result.league.name
                    leagueStandings.value = result.standings.results
                }
                _isRefreshing.emit(false)
            }
        }
    }

    fun getFixture(fixtureId: Int?): GameFixture? {
        return fixturesList.value.find { it.id == fixtureId}
    }

    fun updateLeagues(leagues: List<String>) {
        repository.updateLeagues(leagues)
    }

}