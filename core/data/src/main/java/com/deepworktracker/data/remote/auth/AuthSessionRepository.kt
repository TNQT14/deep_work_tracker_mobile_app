package com.deepworktracker.data.remote.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthSessionRepository {
    val sessionState: StateFlow<AuthSessionState>

    /** Cold start: restore session from secure refresh (+ refresh API). */
    suspend fun bootstrap()

    /** After login/register — tokens already saved via [TokenStore]. */
    fun onLoginSuccess()

    /** Clears tokens and moves to [AuthSessionState.Unauthenticated]. */
    suspend fun logout()

    /** Refresh token rejected while app is running (401/403 from refresh API). */
    suspend fun onRefreshTokenRevoked()

    fun isAuthenticated(): Boolean
}
