package dev.johnoreilly.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameSettingsDto(
    val cup_qualifying_method: String?,
    val cup_start_event_id: Int?,
    val cup_stop_event_id: Int?,
    val cup_type: String?,
    val league_h2h_tiebreak_stats: List<String>,
    val league_join_private_max: Int,
    val league_join_public_max: Int,
    val league_ko_first_instead_of_random: Boolean,
    val league_max_ko_rounds_private_h2h: Int,
    val league_max_size_private_h2h: Int,
    val league_max_size_public_classic: Int,
    val league_max_size_public_h2h: Int,
    val league_points_h2h_draw: Int,
    val league_points_h2h_lose: Int,
    val league_points_h2h_win: Int,
    val league_prefix_public: String,
    val squad_squadplay: Int,
    val squad_squadsize: Int,
    val squad_team_limit: Int,
    val squad_total_spend: Int,
    val stats_form_days: Int,
    val sys_vice_captain_enabled: Boolean,
    val timezone: String,
    val transfers_cap: Int,
    val transfers_sell_on_fee: Double,
    val ui_currency_multiplier: Int,
    val ui_use_special_shirts: Boolean
)