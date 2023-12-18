package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryDto(
    val element: Int,
    val fixture: Int,
    val total_points: Int
)