package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PhaseDto(
    val id: Int,
    val name: String,
    val start_event: Int,
    val stop_event: Int
)