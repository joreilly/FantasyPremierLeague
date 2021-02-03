package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ElementStatDto(
    val label: String,
    val name: String
)