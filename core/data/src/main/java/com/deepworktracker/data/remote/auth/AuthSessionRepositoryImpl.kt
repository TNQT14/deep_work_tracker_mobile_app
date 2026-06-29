package com.deepworktracker.data.remote.auth

import com.deepworktracker.data.remote.token.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionRepositoryImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val refreshTokenCoordinator: RefreshTokenCoordinator,
) : AuthSessionRepository {

    private val _sessionState =
        MutableStateFlow<AuthSessionState>(AuthSessionState.Bootstrapping)
    override val sessionState: StateFlow<AuthSessionState> = _sessionState.asStateFlow()

    override suspend fun bootstrap() {
        _sessionState.value = AuthSessionState.Bootstrapping

        val refresh = tokenStore.getRefreshToken()
        if (refresh.isNullOrBlank()) {
            _sessionState.value = AuthSessionState.Unauthenticated
            return
        }

        _sessionState.value = AuthSessionState.RestoringSession

        _sessionState.value = when (refreshTokenCoordinator.refreshAccessToken(null)) {
            is RefreshResult.Success -> AuthSessionState.Authenticated

            RefreshResult.NoRefreshToken ->
                AuthSessionState.Unauthenticated

            RefreshResult.InvalidRefreshToken -> {
                markSessionExpired()
                return@bootstrap
            }

            RefreshResult.NetworkError ->
                AuthSessionState.BootstrapFailed(BootstrapError.NoInternet)
        }
    }

    override fun onLoginSuccess() {
        _sessionState.value = AuthSessionState.Authenticated
    }

    override suspend fun logout() {
        tokenStore.clear()
        _sessionState.value = AuthSessionState.Unauthenticated
    }

    override suspend fun onRefreshTokenRevoked(){
        markSessionExpired()
    }

    private fun markSessionExpired() {
        tokenStore.clear()
        _sessionState.value =
            AuthSessionState.SessionExpired(SessionExpiredReason.RefreshRevoked)
    }


    override fun isAuthenticated(): Boolean = !tokenStore.getAccessToken().isNullOrBlank()
}
