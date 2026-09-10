package dev.johnoreilly.common.model

/**
 * The squad-construction constraints, as reported by FPL itself rather than hardcoded, so a rule
 * change on their side flows through without a code change.
 */
data class GameRules(
    val budget: Double,
    val squadSize: Int,
    val startingXiSize: Int,
    val clubLimit: Int,
    val sellOnFeePercent: Double,
    val formDays: Int,
    val positions: List<PositionRule>
)

data class PositionRule(
    val elementType: Int,
    val name: String,
    val shortName: String,
    // How many of this position make up the 15-man squad.
    val squadSelect: Int,
    // Min/max that may be fielded in the starting XI, which is what constrains formation.
    val minPlay: Int,
    val maxPlay: Int
)
