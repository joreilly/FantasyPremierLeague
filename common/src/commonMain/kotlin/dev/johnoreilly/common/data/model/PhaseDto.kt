package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PhaseDto(
    val id: Int,
    val name: String,
    val start_event: Int,
    val stop_event: Int
)