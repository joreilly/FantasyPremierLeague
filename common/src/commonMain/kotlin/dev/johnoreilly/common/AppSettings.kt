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

    /**
     * The manager's own entry id. Their leagues are read from it, rather than being typed in one
     * league id at a time - `entry/{id}/` already knows every league they're in.
     */
    val entryId: Flow<Int?> =
        dataStore.data.map { preferences -> preferences[ENTRY_ID_SETTING]?.toIntOrNull() }

    suspend fun updateEntryIdSetting(entryId: Int?) {
        dataStore.edit { preferences ->
            if (entryId == null) {
                preferences.remove(ENTRY_ID_SETTING)
            } else {
                preferences[ENTRY_ID_SETTING] = entryId.toString()
            }
        }
    }

    companion object {
        val ENTRY_ID_SETTING = stringPreferencesKey("entryId")
    }
}