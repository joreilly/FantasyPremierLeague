package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
@Serializable
data class LeagueStandingsDto(
    val league: LeagueDto,
    val standings: LeagueStandingsResultsDto
)