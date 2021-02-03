package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FixtureDto(
    val id: Int,
    val kickoff_time: String?,
    val team_h: Int,
    val team_a: Int,
    val team_h_score: Int?,
    val team_a_score: Int?
)
