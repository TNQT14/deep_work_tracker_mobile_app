package com.deepworktracker.data.remote.interceptor

import com.deepworktracker.data.remote.auth.RefreshResult
import com.deepworktracker.data.remote.auth.RefreshTokenCoordinator
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val refreshTokenCoordinator: RefreshTokenCoordinator,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val previousAuth = response.request.header("Authorization")
        if (previousAuth.isNullOrBlank()) return null
        if (responseCount(response) >= 3) return null

        val previousAccess = previousAuth.removePrefix("Bearer ").trim()

        val result = runBlocking {
            refreshTokenCoordinator.refreshAccessToken(previousAccess)
        }

        val newAccess = when (result) {
            is RefreshResult.Success -> result.accessToken
            else -> return null
        }

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