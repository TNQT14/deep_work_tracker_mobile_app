package com.deepworktracker.data.repository

import com.deepworktracker.data.remote.api.AuthApi
import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.remote.model.request.RegisterRequest
import com.deepworktracker.data.remote.model.response.AuthResponse
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
}
