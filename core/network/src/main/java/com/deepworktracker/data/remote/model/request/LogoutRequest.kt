package com.deepworktracker.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("access_token") val accessToken: String,
)
