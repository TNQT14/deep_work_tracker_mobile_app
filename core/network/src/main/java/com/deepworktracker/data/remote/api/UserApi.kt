package com.deepworktracker.data.remote.api

import com.deepworktracker.data.remote.model.request.UpdateProfileRequest
import com.deepworktracker.data.remote.model.response.ApiEnvelope
import com.deepworktracker.data.remote.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {

    @GET("api/v1/users/me")
    suspend fun getMe(): Response<ApiEnvelope<UserResponse>>

    @PUT("api/v1/users/me")
    suspend fun updateMe(
        @Body body: UpdateProfileRequest,
    ): Response<ApiEnvelope<UserResponse>>
}
