package com.deepworktracker.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val password: String,
)
