package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryPastDto(
    val season_name: String,
    val total_points: Int
)