package com.deepworktracker.data.repository

import com.deepworktracker.data.remote.api.AuthApi
import com.deepworktracker.data.remote.model.request.ForgotPasswordResetRequest
import com.deepworktracker.data.remote.model.request.ForgotPasswordVerifyEmailRequest
import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.remote.model.request.RegisterRequest
import com.deepworktracker.data.remote.model.response.ApiEnvelope
import com.deepworktracker.data.remote.model.response.AuthResponse
import com.deepworktracker.data.remote.model.response.MessageData
import com.deepworktracker.data.remote.network.NetworkResult
import com.deepworktracker.data.remote.network.safeApiCall
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val gson: Gson,
) {

    suspend fun login(
        request: LoginRequest,
    ): NetworkResult<AuthResponse> = safeApiCall(gson) {
        api.login(request)
    }

    suspend fun register(
        request: RegisterRequest,
    ): NetworkResult<AuthResponse> = safeApiCall(gson) {
        api.register(request)
    }

    suspend fun verifyForgotPasswordEmail(
        email: String
    ): NetworkResult<ApiEnvelope<MessageData>> = safeApiCall(gson) {
        api.forgotPasswordVerifyEmail(ForgotPasswordVerifyEmailRequest(email))
    }

    suspend fun resetForgotPassword(
        email: String,
        newPassword: String
    ): NetworkResult<ApiEnvelope<MessageData>> = safeApiCall(gson) {
        api.forgotPasswordReset(ForgotPasswordResetRequest(email, newPassword))
    }
}
