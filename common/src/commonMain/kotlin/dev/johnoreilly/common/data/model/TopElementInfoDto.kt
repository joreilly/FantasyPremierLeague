package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TopElementInfoDto(
    val id: Int,
    val points: Int
)