package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ElementTypeDto(
    val element_count: Int,
    val id: Int,
    val plural_name: String,
    val plural_name_short: String,
    val singular_name: String,
    val singular_name_short: String,
    val squad_max_play: Int,
    val squad_min_play: Int,
    val squad_select: Int,
    val sub_positions_locked: List<Int>,
    val ui_shirt_specific: Boolean
)