package com.deepworktracker.data.remote.auth

sealed interface AuthSessionState {
    /** Reading secure refresh from disk; no network yet. */
    data object Bootstrapping : AuthSessionState

    /** Calling refresh API to mint a new access token. */
    data object RestoringSession : AuthSessionState

    /** Access token is in RAM — user may use the main app. */
    data object Authenticated : AuthSessionState

    /** No stored session — show auth flow. */
    data object Unauthenticated : AuthSessionState

    /** Refresh token rejected; tokens cleared. */
    data class SessionExpired(val reason: SessionExpiredReason) : AuthSessionState

    /** Transient bootstrap failure; refresh token kept for retry. */
    data class BootstrapFailed(val error: BootstrapError) : AuthSessionState
}

enum class SessionExpiredReason { RefreshRevoked }

enum class BootstrapError { NoInternet }
