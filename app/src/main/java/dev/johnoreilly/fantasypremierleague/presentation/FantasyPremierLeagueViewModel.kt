@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)


package dev.johnoreilly.fantasypremierleague.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class FantasyPremierLeagueViewModel(
    private val repository: FantasyPremierLeagueRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val allPlayers = repository.playerList
    val visiblePlayerList: StateFlow<List<Player>> =
        searchQuery.debounce(250).flatMapLatest { searchQuery ->
            allPlayers.mapLatest { playerList ->
                playerList
                    .filter { it.name.contains(searchQuery, ignoreCase = true) }
                    .sortedByDescending { it.points }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val fixturesList = repository.fixtureList

    val leagues: StateFlow<List<String>> = repository.leagues
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var leagueStandings = MutableStateFlow<List<LeagueStandingsDto>>(emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    fun onPlayerSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun getPlayer(playerId: Int): Player? {
        return visiblePlayerList.value.find { it.id == playerId }
    }

    suspend fun getPlayerHistory(playerId: Int): List<PlayerPastHistory> {
        return repository.getPlayerHistoryData(playerId)
    }

    fun getLeagueStandings() {
        viewModelScope.launch {
            if (leagues.value.isNotEmpty()) {
                _isRefreshing.emit(true)

                val leagueStandingsList = mutableListOf<LeagueStandingsDto>()
                leagues.value.forEach { leagueId ->
                    val leagueStandings = repository.getLeagueStandings(leagueId.trim().toInt())
                    leagueStandingsList.add(leagueStandings)
                }
                leagueStandings.value = leagueStandingsList
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