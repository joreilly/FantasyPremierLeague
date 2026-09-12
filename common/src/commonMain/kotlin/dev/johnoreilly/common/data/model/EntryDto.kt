package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

/**
 * A manager's own record, from `entry/{entryId}/`. Public - no session needed - but keyed on the
 * entry id, which a manager reads off their own FPL url. There is no way to look one up by name.
 */
@Serializable
data class EntryDto(
    val id: Int,
    val name: String,
    val player_first_name: String = "",
    val player_last_name: String = "",
    val summary_overall_points: Int? = null,
    val summary_overall_rank: Int? = null,
    // Both in tenths of a million, as with player prices.
    val last_deadline_bank: Int? = null,
    val last_deadline_value: Int? = null,
    val leagues: EntryLeaguesDto = EntryLeaguesDto()
)

/** `cup` is an object rather than a list of leagues, so only the two league lists are modelled. */
@Serializable
data class EntryLeaguesDto(
    val classic: List<EntryLeagueDto> = emptyList(),
    val h2h: List<EntryLeagueDto> = emptyList()
)

@Serializable
data class EntryLeagueDto(
    val id: Int,
    val name: String,
    val short_name: String? = null,
    val entry_rank: Int? = null,
    val entry_last_rank: Int? = null,
    val rank_count: Int? = null,
    // "x" = invitational (a league someone created and invited you to), "s" = joined automatically
    // on signup - your club, your country, your starting gameweek, and Overall.
    val league_type: String = "x",
    val scoring: String = "c",
    val start_event: Int = 1
)
