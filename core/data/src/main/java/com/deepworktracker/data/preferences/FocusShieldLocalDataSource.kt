package com.deepworktracker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.deepworktracker.data.di.FocusShieldDataStore
import com.deepworktracker.domain.model.FocusShieldConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore-backed store for Focus Shield, kept in its OWN file ([FocusShieldDataStore]) rather
 * than the shared user-preferences store. That store rewrites itself with clear()+re-put on every
 * save, which would wipe any foreign key; per-key edits here avoid that entirely.
 */
@Singleton
class FocusShieldLocalDataSource @Inject constructor(
    @FocusShieldDataStore private val dataStore: DataStore<Preferences>,
) {
    fun observeConfig(): Flow<FocusShieldConfig> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            FocusShieldConfig(
                dndEnabled = prefs[PreferencesKeys.SHIELD_DND_ENABLED] ?: false,
                blocklist = prefs[PreferencesKeys.SHIELD_BLOCKLIST] ?: emptySet(),
            )
        }

    suspend fun getConfig(): FocusShieldConfig = observeConfig().first()

    suspend fun setDndEnabled(enabled: Boolean): Result<Unit> = runCatching {
        dataStore.edit { it[PreferencesKeys.SHIELD_DND_ENABLED] = enabled }
        Unit
    }

    suspend fun setBlocklist(packages: Set<String>): Result<Unit> = runCatching {
        dataStore.edit { it[PreferencesKeys.SHIELD_BLOCKLIST] = packages }
        Unit
    }

    suspend fun getPreviousDndFilter(): Int? =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences()) else throw exception
            }
            .first()[PreferencesKeys.SHIELD_PREV_FILTER]

    suspend fun setPreviousDndFilter(filter: Int?): Result<Unit> = runCatching {
        dataStore.edit { prefs ->
            if (filter == null) {
                prefs.remove(PreferencesKeys.SHIELD_PREV_FILTER)
            } else {
                prefs[PreferencesKeys.SHIELD_PREV_FILTER] = filter
            }
        }
        Unit
    }
}
