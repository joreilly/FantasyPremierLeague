package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GameWeekLiveDataDto(
    val elements: List<GameWeekLiveDataElementDto>
)