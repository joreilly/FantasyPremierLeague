package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GameWeekLiveDataElementDto(
    val id: Int,
    val stats: GameWeekLiveDataElementStatsDto
)