package dev.johnoreilly.common.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Team(
    @PrimaryKey val id: Int,
    val index: Int,
    val name: String,
    val code: Int
)
