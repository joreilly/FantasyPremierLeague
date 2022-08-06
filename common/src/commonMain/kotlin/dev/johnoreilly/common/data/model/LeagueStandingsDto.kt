package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LeagueStandingsDto(
    val league: LeagueDto,
    val standings: LeagueStandingsResultsDto
)