package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryDto(
    val element: Int,
    val fixture: Int,
    val total_points: Int,
    val round: Int,
    val goals_scored: Int,
    val assists: Int
)