package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class ElementStat(
    val label: String,
    val name: String
)