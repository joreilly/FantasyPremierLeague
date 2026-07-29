package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

/** An entry from the FPL /api/regions/ endpoint (player nationality lookup). */
@Serializable
data class RegionDto(
    val id: Int,
    val name: String,
    val iso_code_short: String? = null,
    val iso_code_long: String? = null,
)
