package dev.johnoreilly.common.model

import kotlinx.serialization.Serializable

@Serializable
data class BootstrapStaticInfo(
    val element_stats: List<ElementStat>,
    val element_types: List<ElementType>,
    val elements: List<Element>,
    val events: List<Event>,
    val game_settings: GameSettings,
    val phases: List<Phase>,
    val teams: List<Team>,
    val total_players: Int
)