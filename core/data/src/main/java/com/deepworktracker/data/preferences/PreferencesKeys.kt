package com.deepworktracker.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
    val HAS_EXPLICIT_THEME = booleanPreferencesKey("has_explicit_theme")
    val HAS_EXPLICIT_LANGUAGE = booleanPreferencesKey("has_explicit_language")
    val UPDATED_AT = longPreferencesKey("update_at")
    val SYNC_VERSION = longPreferencesKey("sync_version")
    val SCOPE = stringPreferencesKey("scope")
    val RESPECT_SYSTEM_FONT_SCALE = booleanPreferencesKey("respect_system_font_scale")
    val RESPECT_REDUCED_MOTION = booleanPreferencesKey("respect_reduced_motion")
    val FORCE_HIGH_CONTRAST = booleanPreferencesKey("force_high_contrast")
    val SHIELD_DND_ENABLED = booleanPreferencesKey("shield_dnd_enabled")
    val SHIELD_BLOCKLIST = stringSetPreferencesKey("shield_blocklist")
    val SHIELD_PREV_FILTER = intPreferencesKey("shield_prev_dnd_filter")
}