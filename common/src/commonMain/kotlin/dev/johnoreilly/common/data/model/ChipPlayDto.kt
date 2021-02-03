package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChipPlayDto(
    val chip_name: String,
    val num_played: Int
)