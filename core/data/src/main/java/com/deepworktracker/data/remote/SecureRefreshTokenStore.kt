package com.deepworktracker.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypted, on-disk store for the refresh token — the single credential the app relies on
 * to restore a session after the process is killed (the access token lives only in RAM).
 *
 * Reliability rules (fix for spurious re-login after kill):
 * - Writes use [SharedPreferences.Editor.commit] (synchronous) so the token is durable even if
 *   the app is killed immediately after login or a token rotation.
 * - Transient Keystore / decrypt failures are retried before giving up, so a flaky read is not
 *   mistaken for "no session".
 * - The whole encrypted file is only wiped as a genuine last resort (real keyset corruption),
 *   never on the first transient exception — so a valid token is never destroyed by a hiccup.
 */
@Singleton
class SecureRefreshTokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Volatile
    private var cachedPrefs: SharedPreferences? = null

    /**
     * Encrypted prefs handle, built lazily but kept in a resettable field (not `by lazy`) so a
     * transient keystore failure can drop the handle and rebuild without caching a broken state.
     */
    private fun prefs(): SharedPreferences {
        cachedPrefs?.let { return it }
        return synchronized(this) {
            cachedPrefs ?: createPrefs().also { cachedPrefs = it }
        }
    }

    /**
     * Returns the stored refresh token, or null **only when it is genuinely absent**.
     * Transient read failures are retried (rebuilding the handle) before returning null, so a
     * flaky decrypt does not masquerade as "no session" and force a re-login.
     */
    fun read(): String? {
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                return prefs().getString(KEY_REFRESH_TOKEN, null)
            } catch (_: Exception) {
                dropCachedPrefs()
                if (attempt == MAX_ATTEMPTS - 1) return null
            }
        }
        return null
    }

    /**
     * Persists the refresh token synchronously so it survives an immediate app kill.
     * Returns true on success. Callers should invoke this off the main thread.
     */
    fun write(refreshToken: String): Boolean {
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                return prefs().edit().putString(KEY_REFRESH_TOKEN, refreshToken).commit()
            } catch (_: Exception) {
                dropCachedPrefs()
                if (attempt == MAX_ATTEMPTS - 1) return false
            }
        }
        return false
    }

    fun clear() {
        try {
            prefs().edit().remove(KEY_REFRESH_TOKEN).commit()
        } catch (_: Exception) {
            // Clear is meant to remove the token anyway — deleting the file here is acceptable.
            dropCachedPrefs()
            runCatching { context.deleteSharedPreferences(FILE_NAME) }
        }
    }

    private fun dropCachedPrefs() {
        synchronized(this) { cachedPrefs = null }
    }

    /**
     * Builds the encrypted prefs, retrying transient failures. Only when **every** attempt fails
     * (genuine keyset corruption, e.g. after restore to a new device) does it wipe and rebuild —
     * so a transient keystore hiccup no longer destroys a valid refresh token.
     */
    private fun createPrefs(): SharedPreferences {
        var lastError: Exception? = null
        repeat(MAX_ATTEMPTS) {
            try {
                return buildEncryptedPrefs()
            } catch (e: Exception) {
                lastError = e
            }
        }
        return try {
            context.deleteSharedPreferences(FILE_NAME)
            buildEncryptedPrefs()
        } catch (e: Exception) {
            throw lastError ?: e
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
        private const val MAX_ATTEMPTS = 3
    }
}
