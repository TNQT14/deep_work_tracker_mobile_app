package com.deepworktracker.data.remote.api

import com.deepworktracker.data.remote.model.request.RefreshTokenRequest
import com.deepworktracker.data.remote.model.response.ApiEnvelope
import com.deepworktracker.data.remote.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * `POST /api/v1/auth/refresh` does not exist on the backend yet (confirmed 404 — the Go router
 * only registers login/register/logout/forgot-password). Wired for when it ships; the envelope
 * shape below matches every other endpoint on this backend, not a confirmed refresh contract.
 */
interface TokenRefreshApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body body: RefreshTokenRequest,
    ): Response<ApiEnvelope<AuthResponse>>
}
