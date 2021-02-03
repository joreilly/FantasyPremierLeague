package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val code: Int,
    val draw: Int,
    val form: String?,
    val id: Int,
    val loss: Int,
    val name: String,
    val played: Int,
    val points: Int,
    val position: Int,
    val pulse_id: Int,
    val short_name: String,
    val strength: Int,
    val strength_attack_away: Int,
    val strength_attack_home: Int,
    val strength_defence_away: Int,
    val strength_defence_home: Int,
    val strength_overall_away: Int,
    val strength_overall_home: Int,
    val unavailable: Boolean,
    val win: Int
)