package dev.johnoreilly.fantasypremierleague.ui.players

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import co.touchlab.kermit.Kermit
import dev.johnoreilly.common.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.repository.GameFixture
import dev.johnoreilly.common.repository.Player
import kotlinx.coroutines.launch


class PlayersViewModel(
    private val repository: FantasyPremierLeagueRepository,
    private val logger: Kermit
) : ViewModel() {
    val fixtures = MutableLiveData<List<GameFixture>>(emptyList())

    val players = MutableLiveData<List<Player>>()
    val query = mutableStateOf("")

    init {
        viewModelScope.launch {
            fixtures.value = repository.getFixtures()
            players.value = repository.getPlayers()
        }
    }

    fun onPlayerSearchQueryChange(query: String) {
        viewModelScope.launch {
            players.value = repository
                .getPlayers()
                .filter { player -> player.name.toLowerCase().contains(query.toLowerCase()) }
        }
    }
}