package com.deepworktracker.data.remote.api

import com.deepworktracker.data.remote.model.request.RefreshTokenRequest
import com.deepworktracker.data.remote.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Wired for refresh flow; enable when `/api/v1/auth/refresh` exists on the backend.
 */
interface TokenRefreshApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body body: RefreshTokenRequest,
    ): Response<AuthResponse>
}
