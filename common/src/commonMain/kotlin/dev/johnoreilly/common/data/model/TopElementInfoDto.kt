package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TopElementInfoDto(
    val id: Int,
    val points: Int
)