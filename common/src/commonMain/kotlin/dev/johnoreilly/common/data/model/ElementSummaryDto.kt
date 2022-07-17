package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ElementSummaryDto(
    val history_past: List<HistoryPastDto>
)