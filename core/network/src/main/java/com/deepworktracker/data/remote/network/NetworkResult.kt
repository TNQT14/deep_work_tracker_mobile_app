package com.deepworktracker.data.remote.network

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
