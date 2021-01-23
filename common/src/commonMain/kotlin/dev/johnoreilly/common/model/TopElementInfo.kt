package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class TopElementInfo(
    val id: Int,
    val points: Int
)