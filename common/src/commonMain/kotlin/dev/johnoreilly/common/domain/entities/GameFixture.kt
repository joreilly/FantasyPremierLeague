package dev.johnoreilly.common.domain.entities

data class GameFixture(
    val id: Int,
    val kickoffTime: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamPhotoUrl: String,
    val awayTeamPhotoUrl: String,
    val homeTeamScore: Int,
    val awayTeamScore: Int
)