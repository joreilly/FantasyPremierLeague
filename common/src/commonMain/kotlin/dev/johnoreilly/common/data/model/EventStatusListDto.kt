package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
@Serializable
data class EventStatusListDto(
    val status: List<EventStatusDto>
)