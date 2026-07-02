package com.example.todo.presentation.countdown

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.countdownDataStore by preferencesDataStore(name = "countdown_state")

data class CountdownSavedState(
    val todoId: String,
    val sessionId: String,
    val endEpochMillis: Long,
    val totalSeconds: Int,
)

@Singleton
class CountdownStateStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val KEY_TODO_ID = stringPreferencesKey("todo_id")
        private val KEY_SESSION_ID = stringPreferencesKey("session_id")
        private val KEY_END_EPOCH = longPreferencesKey("end_epoch_millis")
        private val KEY_TOTAL_SECONDS = intPreferencesKey("total_seconds")
    }

    suspend fun save(todoId: String, sessionId: String, endEpochMillis: Long, totalSeconds: Int) {
        context.countdownDataStore.edit { prefs ->
            prefs[KEY_TODO_ID] = todoId
            prefs[KEY_SESSION_ID] = sessionId
            prefs[KEY_END_EPOCH] = endEpochMillis
            prefs[KEY_TOTAL_SECONDS] = totalSeconds
        }
    }

    suspend fun load(): CountdownSavedState? {
        val prefs = context.countdownDataStore.data.first()
        val todoId = prefs[KEY_TODO_ID] ?: return null
        val sessionId = prefs[KEY_SESSION_ID] ?: return null
        val endEpoch = prefs[KEY_END_EPOCH] ?: return null
        val totalSeconds = prefs[KEY_TOTAL_SECONDS] ?: return null
        return CountdownSavedState(todoId, sessionId, endEpoch, totalSeconds)
    }

    suspend fun clear() {
        context.countdownDataStore.edit { it.clear() }
    }
}
