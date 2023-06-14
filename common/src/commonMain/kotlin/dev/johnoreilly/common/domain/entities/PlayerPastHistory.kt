package dev.johnoreilly.common.domain.entities

data class PlayerPastHistory(
    val seasonName: String,
    val totalPoints: Int,
    val totalGoals: Int,
    val totalAssists: Int

)