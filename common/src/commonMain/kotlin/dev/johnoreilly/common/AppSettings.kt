package dev.johnoreilly.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath


fun createDataStore(
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { producePath().toPath() },
)

class AppSettings(private val dataStore: DataStore<Preferences>) {

    val leagues: Flow<List<String>> =
        dataStore.data
            .map { preferences ->
                getLeaguesSettingFromString(preferences[LEAGUES_SETTING])
            }


    suspend fun updatesLeaguesSetting(leagues: List<String>) {
        dataStore.edit { preferences ->
            preferences[LEAGUES_SETTING] = leagues.joinToString(separator = ",")
        }
    }


    private fun getLeaguesSettingFromString(settingsString: String?) =
        settingsString?.split(",") ?: emptyList()

    companion object {
        val LEAGUES_SETTING = stringPreferencesKey("leagues")
    }
}