package dev.johnoreilly.common

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalSettingsApi::class)
class AppSettings(val settings: ObservableSettings) {

    val leagues: Flow<List<String>> =
        settings.getStringOrNullFlow(LEAGUES_SETTING).map { getLeaguesSettingFromString(it) }


    fun updatesLeagesSetting(leagues: List<String>) {
        settings.putString(LEAGUES_SETTING, leagues.joinToString(separator = ","))
    }


    private fun getLeaguesSettingFromString(settingsString: String?) =
        settingsString?.split(",") ?: emptyList()

    companion object {
        const val LEAGUES_SETTING = "leagues"
    }
}