package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ElementStatDto(
    val label: String,
    val name: String
)