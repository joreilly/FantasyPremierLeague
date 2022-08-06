package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EventStatusListDto(
    val status: List<EventStatusDto>
)