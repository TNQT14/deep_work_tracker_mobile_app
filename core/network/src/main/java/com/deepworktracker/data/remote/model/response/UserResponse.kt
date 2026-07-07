package com.deepworktracker.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: String,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val role: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)
