package dev.johnoreilly.fantasypremierleague.ui

import androidx.lifecycle.*
import co.touchlab.kermit.Kermit
import dev.johnoreilly.common.model.BootstrapStaticInfo
import dev.johnoreilly.common.remote.Fixture
import dev.johnoreilly.common.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.repository.Player
import kotlinx.coroutines.launch



class FantasyPremierLeagueViewModel(
    private val repoitory: FantasyPremierLeagueRepository,
    private val logger: Kermit
) : ViewModel() {

    val fixtures = MutableLiveData<List<Fixture>>(emptyList())
    val staticData = MutableLiveData<BootstrapStaticInfo>()

    val players = MutableLiveData<List<Player>>()

    init {
        viewModelScope.launch {
            fixtures.value = repoitory.fetchFixtures()

            val bootstrapStaticInfo = repoitory.fetchBootstrapStaticInfo()

            staticData.value = bootstrapStaticInfo

            players.value = repoitory.getPlayers()
        }
    }
}