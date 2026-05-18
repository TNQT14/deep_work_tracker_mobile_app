package com.deepworktracker.data.remote.interceptor

import com.deepworktracker.data.remote.api.TokenRefreshApi
import com.deepworktracker.data.remote.model.request.RefreshTokenRequest
import com.deepworktracker.data.remote.token.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * On 401 for requests that already carry Authorization, attempts refresh synchronously
 * (see backend `POST /api/v1/auth/refresh`). Skips when no refresh token is stored.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val tokenRefreshApi: TokenRefreshApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization").isNullOrBlank()) {
            return null
        }
        if (responseCount(response) >= 3) {
            return null
        }
        val refresh = tokenStore.getRefreshToken().orEmpty()
        if (refresh.isBlank()) {
            return null
        }

        val tokens = runBlocking {
            try {
                val refreshResponse = tokenRefreshApi.refresh(
                    RefreshTokenRequest(refreshToken = refresh),
                )
                if (refreshResponse.isSuccessful) {
                    refreshResponse.body()
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        } ?: return null

        val newAccess = tokens.accessToken.orEmpty()
        if (newAccess.isBlank()) {
            return null
        }

        tokenStore.setTokens(
            access = newAccess,
            refresh = tokens.refreshToken ?: refresh,
        )

        return response.request.newBuilder()
            .removeHeader("Authorization")
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
