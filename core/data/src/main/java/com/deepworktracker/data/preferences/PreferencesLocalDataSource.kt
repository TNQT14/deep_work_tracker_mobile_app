package com.deepworktracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.deepworktracker.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesLocalDataSource @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext context: Context,
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile(STORE_NAME)
        },
    )

    companion object {
        private const val STORE_NAME = "user_preferences"
    }

    fun observePreferences(): Flow<UserPreferences> = dataStore.data.catch { exception ->
        if (
            exception is IOException
        ) {
            emit(androidx.datastore.preferences.core.emptyPreferences())
        } else {
            throw exception
        }
    }.map(
        UserPreferencesMapper::toDomain
    )

    suspend fun getPreferences(): UserPreferences = observePreferences().first()

    suspend fun savePreferences(preferences: UserPreferences): Result<Unit> {
        return try {
            dataStore.edit { prefs ->
                val mapped = UserPreferencesMapper.toPreferences(preferences)
                prefs.clear()
                prefs += mapped
            }
            Result.success(Unit)
        } catch (
            e: Exception
        ) {
            Result.failure(e)
        }
    }

    suspend fun updatePreferences(
        transform: (UserPreferences) -> UserPreferences,
    ): Result<Unit> {
        return try {
            dataStore.edit { prefs ->
                val current = UserPreferencesMapper.toDomain(prefs)
                val updated = transform(current)
                val mapped = UserPreferencesMapper.toPreferences(updated)
                prefs.clear()
                prefs += mapped
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}