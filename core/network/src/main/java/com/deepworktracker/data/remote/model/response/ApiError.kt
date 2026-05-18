package com.deepworktracker.data.remote.model.response

import com.google.gson.JsonElement

data class ApiError(
    val message: String? = null,
    val code: String? = null,
    val details: JsonElement? = null,
)
