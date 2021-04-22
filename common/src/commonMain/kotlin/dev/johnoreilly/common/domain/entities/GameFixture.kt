package dev.johnoreilly.common.domain.entities

import kotlinx.datetime.LocalDateTime

data class GameFixture(
    val id: Int,
    val localKickoffTime: LocalDateTime,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamPhotoUrl: String,
    val awayTeamPhotoUrl: String,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?
)