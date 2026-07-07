package com.deepworktracker.data.repository

import com.deepworktracker.data.remote.api.UserApi
import com.deepworktracker.data.remote.model.request.UpdateProfileRequest
import com.deepworktracker.data.remote.model.response.UserResponse
import com.deepworktracker.data.remote.network.NetworkResult
import com.deepworktracker.data.remote.network.safeApiCall
import com.deepworktracker.data.remote.network.unwrapEnvelope
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: UserApi,
    private val gson: Gson,
) {

    suspend fun getMe(): NetworkResult<UserResponse> = safeApiCall(gson) {
        api.getMe()
    }.unwrapEnvelope()

    suspend fun updateMe(
        fullName: String,
        password: String,
    ): NetworkResult<UserResponse> = safeApiCall(gson) {
        api.updateMe(UpdateProfileRequest(fullName = fullName, password = password))
    }.unwrapEnvelope()
}
