package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class ElementSummaryDto(
    val history: List<HistoryDto>,
    val history_past: List<HistoryPastDto>
)