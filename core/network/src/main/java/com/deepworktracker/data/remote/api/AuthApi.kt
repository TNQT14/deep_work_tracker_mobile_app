package com.deepworktracker.data.remote.api

import com.deepworktracker.data.remote.model.request.ForgotPasswordResetRequest
import com.deepworktracker.data.remote.model.request.ForgotPasswordVerifyEmailRequest
import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.remote.model.request.LogoutRequest
import com.deepworktracker.data.remote.model.request.RegisterRequest
import com.deepworktracker.data.remote.model.response.ApiEnvelope
import com.deepworktracker.data.remote.model.response.AuthResponse
import com.deepworktracker.data.remote.model.response.MessageData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Backend wraps every response as {"success": bool, "data": {...}} — verified against the
    // real login/register responses. AuthResponse alone (unwrapped) always deserializes to all
    // nulls, so nothing was ever persisted after login.
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest,
    ): Response<ApiEnvelope<AuthResponse>>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequest,
    ): Response<ApiEnvelope<AuthResponse>>

    @POST("api/v1/auth/forgot-password/verify-email")
    suspend fun forgotPasswordVerifyEmail(
        @Body body: ForgotPasswordVerifyEmailRequest,
    ): Response<ApiEnvelope<MessageData>>

    @POST("api/v1/auth/forgot-password/reset")
    suspend fun forgotPasswordReset(
        @Body body: ForgotPasswordResetRequest,
    ): Response<ApiEnvelope<MessageData>>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body body: LogoutRequest,
    ): Response<ApiEnvelope<MessageData>>
}