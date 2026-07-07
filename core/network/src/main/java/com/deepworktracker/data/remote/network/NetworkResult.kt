package com.deepworktracker.data.remote.network

import com.deepworktracker.data.remote.model.response.ApiEnvelope

sealed class NetworkResult<out T> {

    data class Success<T>(val data: T) : NetworkResult<T>()

    data class HttpError(val code: Int, val message: String?) : NetworkResult<Nothing>()

    data object Unauthorized : NetworkResult<Nothing>()

    data object NotFound : NetworkResult<Nothing>()

    data object ServerError : NetworkResult<Nothing>()

    data object NoInternet : NetworkResult<Nothing>()

    data class ParseError(val cause: Throwable) : NetworkResult<Nothing>()

    data class NetworkError(val message: String?) : NetworkResult<Nothing>()
}

/**
 * Unwraps the backend's `{"success": bool, "data": {...}}` envelope on top of an already-mapped
 * [NetworkResult]. HTTP-level failures pass through unchanged; only a 2xx response with
 * `success=false` or a missing `data` is downgraded to [NetworkResult.ParseError].
 */
@Suppress("UNCHECKED_CAST")
fun <T> NetworkResult<ApiEnvelope<T>>.unwrapEnvelope(): NetworkResult<T> = when (this) {
    is NetworkResult.Success -> {
        val payload = data.data
        if (data.success && payload != null) {
            NetworkResult.Success(payload)
        } else {
            NetworkResult.ParseError(IllegalStateException("Envelope missing data: success=${data.success}"))
        }
    }
    // NetworkResult<Nothing> variants — safe to reuse as NetworkResult<T>, no ApiEnvelope payload involved.
    else -> this as NetworkResult<T>
}
