package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameWeekLiveDataElementStatsDto(
    val minutes: Int,
    val goals_scored: Int,
    val assists: Int,
    val clean_sheets: Int,
    val goals_conceded: Int,
    val bonus: Int,
    val influence: String,
    val creativity: String,
    val threat: String,
    val total_points: Int
)