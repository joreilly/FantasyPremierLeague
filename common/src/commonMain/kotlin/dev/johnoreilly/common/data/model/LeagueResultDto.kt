package dev.johnoreilly.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueResultDto(
    // The manager's entry id. FPL used to send this as "id"; requiring that name made every
    // standings request fail to parse once they renamed it, which left the leagues screen blank.
    // Everything here is defaulted so a future rename degrades a column rather than the screen.
    @SerialName("entry")
    val entryId: Int? = null,
    val rank: Int = 0,
    @SerialName("last_rank")
    val lastRank: Int = 0,
    @SerialName("event_total")
    val eventTotal: Int = 0,
    val total: Int = 0,
    @SerialName("player_name")
    val playerName: String = "",
    @SerialName("entry_name")
    val entryName: String = ""
)
