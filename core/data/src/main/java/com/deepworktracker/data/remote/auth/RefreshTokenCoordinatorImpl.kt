package com.deepworktracker.data.remote.auth

import com.deepworktracker.data.remote.api.TokenRefreshApi
import com.deepworktracker.data.remote.model.request.RefreshTokenRequest
import com.deepworktracker.data.remote.token.TokenStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class RefreshTokenCoordinatorImpl @Inject constructor(
    private val tokenStore: TokenStore,
    private val tokenRefreshApi: TokenRefreshApi,
    private val authSessionRepository: Provider<AuthSessionRepository>,
): RefreshTokenCoordinator{

    private val mutex = Mutex()

    override suspend fun refreshAccessToken(previousAccessToken: String?): RefreshResult =
        mutex.withLock {
            val current = tokenStore.getAccessToken()
            if (previousAccessToken != null &&
                !current.isNullOrBlank() &&
                current != previousAccessToken
            ) {
                return@withLock RefreshResult.Success(current)
            }
            val refresh = tokenStore.getRefreshToken()
            if (refresh.isNullOrBlank()) {
                return@withLock RefreshResult.NoRefreshToken
            }
            try {
                val response = tokenRefreshApi.refresh(
                    RefreshTokenRequest(refreshToken = refresh),
                )
                when {
                    response.isSuccessful -> {
                        val body = response.body()
                        val newAccess = body?.accessToken
                        if (newAccess.isNullOrBlank()) {
                            RefreshResult.NetworkError
                        } else {
                            tokenStore.setTokens(
                                access = newAccess,
                                refresh = body.refreshToken ?: refresh,
                            )
                            RefreshResult.Success(newAccess)
                        }
                    }
                    response.code() == 401 || response.code() == 403 ->{
                        authSessionRepository.get().onRefreshTokenRevoked()
                        RefreshResult.InvalidRefreshToken
                    }

                    else -> RefreshResult.NetworkError
                }
            } catch (_: Exception) {
                RefreshResult.NetworkError
            }
        }


}