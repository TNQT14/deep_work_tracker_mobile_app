package com.deepworktracker.data.remote.model.response

import com.google.gson.JsonElement

/**
 * Real error body is `{"success": false, "error": "..."}` — the `error` key, not `message`.
 * Both are kept so any endpoint that does use `message` still parses correctly.
 */
data class ApiError(
    val error: String? = null,
    val message: String? = null,
    val code: String? = null,
    val details: JsonElement? = null,
)
