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
    val assists: Int
)