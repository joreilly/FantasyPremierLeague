package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LeagueStandingsResultsDto(
    val results: List<LeagueResultDto>
)