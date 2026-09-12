package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.data.model.EventStatusDto
import dev.johnoreilly.common.data.model.LeagueStandingsDto
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.League
import dev.johnoreilly.common.model.Manager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/** A league the manager is in, paired with its current table. */
data class LeagueWithStandings(
    val league: League,
    val standings: LeagueStandingsDto
)

open class LeaguesViewModel(
    private val repository: FantasyPremierLeagueRepository
) : ViewModel(), KoinComponent {

    val entryId: StateFlow<Int?> = repository.entryId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val manager: StateFlow<Manager?> = repository.entryId
        .map { id -> id?.let { runCatching { repository.getManager(it) }.getOrNull() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Tables for the manager's invitational leagues only. The automatically-joined ones (Overall,
     * their country, their club) run to millions of entries, where a table of the top 50 strangers
     * says nothing useful - those are still listed, just not fetched.
     *
     * Fetched concurrently: a manager in a dozen leagues would otherwise wait on a dozen
     * round trips one after another.
     */
    val leagueStandings: StateFlow<List<LeagueWithStandings>> = manager
        .map { manager ->
            val leagues = manager?.leagues.orEmpty().filter { it.isInvitational }
            coroutineScope {
                leagues
                    .map { league ->
                        async {
                            runCatching { repository.getLeagueStandings(league.id) }
                                .getOrNull()
                                ?.let { LeagueWithStandings(league, it) }
                        }
                    }
                    .awaitAll()
                    .filterNotNull()
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateEntryId(entryId: Int?) {
        viewModelScope.launch {
            repository.updateEntryId(entryId)
        }
    }

    suspend fun getEventStatus(): List<EventStatusDto> {
        return repository.getEventStatus().status
    }
}
