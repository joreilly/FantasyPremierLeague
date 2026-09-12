package dev.johnoreilly.common.model

/** A manager and the leagues they're in, as returned in a single `entry/{id}/` call. */
data class Manager(
    val id: Int,
    val teamName: String,
    val playerName: String,
    val overallPoints: Int?,
    val overallRank: Int?,
    val bank: Double?,
    val teamValue: Double?,
    val leagues: List<League>
)

data class League(
    val id: Int,
    val name: String,
    /** This manager's current rank in the league, and their rank last gameweek. */
    val entryRank: Int?,
    val lastRank: Int?,
    /** How many managers are in the league. */
    val entryCount: Int?,
    /** A league someone created and invited people to, as opposed to one joined automatically. */
    val isInvitational: Boolean,
    val isHeadToHead: Boolean
)
