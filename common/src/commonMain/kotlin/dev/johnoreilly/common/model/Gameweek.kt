package dev.johnoreilly.common.model

/**
 * A single gameweek ("event" in FPL's own vocabulary). Held in memory rather than the database -
 * it's small, and only ever read alongside a fresh bootstrap fetch.
 */
data class Gameweek(
    val id: Int,
    val name: String,
    val deadlineTime: String,
    val isCurrent: Boolean,
    val isNext: Boolean,
    val finished: Boolean,
    val averageEntryScore: Int,
    val highestScore: Int?
)
