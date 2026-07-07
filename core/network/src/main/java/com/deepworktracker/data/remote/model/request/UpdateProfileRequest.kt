package com.deepworktracker.data.remote.model.request

import com.google.gson.annotations.SerializedName

/**
 * Empty string means "no change" for both fields — matches the backend's
 * `omitempty` binding on `full_name`/`password` in `PUT /api/v1/users/me`.
 */
data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String = "",
    val password: String = "",
)
