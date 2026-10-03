package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ChipPlayDto(
    val chip_name: String,
    val num_played: Int
)