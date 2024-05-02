package dev.johnoreilly.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
@Entity
data class GameFixture(
    @PrimaryKey val id: Int,
    val localKickoffTime: LocalDateTime,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamPhotoUrl: String,
    val awayTeamPhotoUrl: String,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?,
    val event: Int
)