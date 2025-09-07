@file:OptIn(ExperimentalTime::class)

package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class FixtureDto(
    val id: Int,
    val event: Int?,
    val kickoff_time: Instant?,
    val team_h: Int,
    val team_a: Int,
    val team_h_score: Int?,
    val team_a_score: Int?,
    val team_h_difficulty: Int,
    val team_a_difficulty: Int
)
