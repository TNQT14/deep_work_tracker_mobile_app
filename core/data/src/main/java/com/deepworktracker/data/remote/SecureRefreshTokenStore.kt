package com.deepworktracker.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureRefreshTokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        createPrefs()
    }

    fun read(): String? = try {
        prefs.getString(KEY_REFRESH_TOKEN, null)
    } catch (e: Exception) {
        null
    }

    fun write(refreshToken: String) {
        try {
            prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        } catch (e: Exception) {

        }
    }

    fun clear() {
        try {
            prefs.edit().remove(KEY_REFRESH_TOKEN).apply()
        } catch (_: Exception) {
            context.deleteSharedPreferences(FILE_NAME)
        }
    }


    private fun createPrefs(): SharedPreferences {
        return try {
            buildEncryptedPrefs()
        } catch (_: Exception) {
            context.deleteSharedPreferences(FILE_NAME)
            buildEncryptedPrefs()
        }
    }

    private fun buildEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    companion object {
        const val FILE_NAME = "auth_secure_prefs"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}