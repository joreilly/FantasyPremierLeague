package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Phase(
    val id: Int,
    val name: String,
    val start_event: Int,
    val stop_event: Int
)