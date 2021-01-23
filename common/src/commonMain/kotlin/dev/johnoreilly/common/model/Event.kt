package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val average_entry_score: Int,
    val chip_plays: List<ChipPlay>,
    val data_checked: Boolean,
    val deadline_time: String,
    val deadline_time_epoch: Int,
    val deadline_time_game_offset: Int,
    val finished: Boolean,
    val highest_score: Int?,
    val highest_scoring_entry: Int?,
    val id: Int,
    val is_current: Boolean,
    val is_next: Boolean,
    val is_previous: Boolean,
    val most_captained: Int?,
    val most_selected: Int?,
    val most_transferred_in: Int?,
    val most_vice_captained: Int?,
    val name: String,
    val top_element: Int?,
    val top_element_info: TopElementInfo?,
    val transfers_made: Int
)