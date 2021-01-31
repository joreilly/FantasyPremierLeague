package dev.johnoreilly.fantasypremierleague.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import co.touchlab.kermit.Kermit
import dev.johnoreilly.common.model.BootstrapStaticInfo
import dev.johnoreilly.common.remote.Fixture
import dev.johnoreilly.common.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.repository.Player
import kotlinx.coroutines.launch


class FantasyPremierLeagueViewModel(
    private val repository: FantasyPremierLeagueRepository,
    private val logger: Kermit
) : ViewModel() {
    val fixtures = MutableLiveData<List<Fixture>>(emptyList())
    val staticData = MutableLiveData<BootstrapStaticInfo>()

    val players = MutableLiveData<List<Player>>()
    val query = mutableStateOf("")

    init {
        viewModelScope.launch {
            fixtures.value = repository.fetchFixtures()
            val bootstrapStaticInfo = repository.fetchBootstrapStaticInfo()
            staticData.value = bootstrapStaticInfo
            players.value = repository.getPlayers()
        }
    }

    fun onPlayerSearch(query: String) {
        viewModelScope.launch {
            players.value = repository
                .getPlayers()
                .filter { player -> player.name.toLowerCase().contains(query.toLowerCase()) }
        }
    }
}