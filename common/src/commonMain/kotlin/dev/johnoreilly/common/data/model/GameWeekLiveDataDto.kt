package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameWeekLiveDataDto(
    val elements: List<GameWeekLiveDataElementDto>
)