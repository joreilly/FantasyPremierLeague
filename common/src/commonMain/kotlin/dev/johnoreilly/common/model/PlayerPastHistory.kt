package dev.johnoreilly.common.model
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
data class PlayerPastHistory(
    val seasonName: String,
    val totalPoints: Int

)