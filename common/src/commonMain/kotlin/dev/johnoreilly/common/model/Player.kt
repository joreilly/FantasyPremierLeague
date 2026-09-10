package dev.johnoreilly.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Player(
    @PrimaryKey val id: Int,
    val name: String,
    val team: String,
    val photoUrl: String,
    val points: Int,
    val currentPrice: Double,
    val goalsScored: Int,
    val assists: Int,
    val nationality: String? = null,
    val elementType: Int = 0,
    val teamId: Int = 0,
    val status: String = "a",
    val chanceOfPlayingNextRound: Int? = null,
    // Short display name ("Saka"), as opposed to the full "Bukayo Saka" in [name].
    val webName: String = "",
    // Points scored per match over the last `stats_form_days` days - FPL's own recent-form measure.
    val form: Double = 0.0,
    val pointsPerGame: Double = 0.0,
    val minutes: Int = 0,
    val selectedByPercent: Double = 0.0,
    // FPL's expected points for the next gameweek. Null before the fixture is known.
    val expectedPointsNext: Double? = null,
    // Injury/availability note, empty when there's nothing to report.
    val news: String = "",
    val bonus: Int = 0,
    val bps: Int = 0,
    val cleanSheets: Int = 0,
    val goalsConceded: Int = 0,
    val saves: Int = 0,
    // Set-piece duty: 1 = first choice. Null when the player isn't on that duty at all.
    val penaltiesOrder: Int? = null,
    val directFreekicksOrder: Int? = null,
    val cornersOrder: Int? = null,
    val expectedGoalInvolvementsPer90: Double = 0.0
)
