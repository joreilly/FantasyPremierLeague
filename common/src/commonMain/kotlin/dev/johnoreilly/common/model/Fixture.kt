package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Fixture(val id: Int, val kickoff_time: String?, val team_h: Int, val team_a: Int)
