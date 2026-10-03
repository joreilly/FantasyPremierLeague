package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
@Serializable
data class LeagueDto(
    val name: String
)