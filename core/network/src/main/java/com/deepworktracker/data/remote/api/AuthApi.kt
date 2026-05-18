package com.deepworktracker.data.remote.api

import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.remote.model.request.RegisterRequest
import com.deepworktracker.data.remote.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest,
    ): Response<AuthResponse>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
    )
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequest,
    ): Response<AuthResponse>
}
