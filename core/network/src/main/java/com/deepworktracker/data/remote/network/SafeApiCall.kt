package com.deepworktracker.data.remote.network

import com.deepworktracker.data.remote.model.response.ApiError
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    gson: Gson,
    apiToBeCalled: suspend () -> Response<T>,
): NetworkResult<T> {
    return try {
        val response = apiToBeCalled()
        mapResponse(gson, response)
    } catch (_: UnknownHostException) {
        NetworkResult.NoInternet
    } catch (e: SocketTimeoutException) {
        NetworkResult.NetworkError(e.message ?: e.javaClass.simpleName)
    } catch (e: MalformedJsonException) {
        NetworkResult.ParseError(e)
    } catch (e: JsonParseException) {
        NetworkResult.ParseError(e)
    } catch (e: IOException) {
        NetworkResult.NetworkError(e.message ?: e.javaClass.simpleName)
    }
}

internal fun <T> mapResponse(
    gson: Gson,
    response: Response<T>,
): NetworkResult<T> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.ParseError(
                IllegalStateException("Successful response with empty body"),
            )
        }
    } else {
        val message = parseErrorMessage(gson, response.errorBody())
        when (response.code()) {
            401 -> NetworkResult.Unauthorized
            404 -> NetworkResult.NotFound
            in 500..599 -> NetworkResult.ServerError
            else -> NetworkResult.HttpError(response.code(), message)
        }
    }
}

private fun parseErrorMessage(
    gson: Gson,
    errorBody: ResponseBody?,
): String? {
    val raw = errorBody?.string() ?: return null
    val apiError = try {
        gson.fromJson(raw, ApiError::class.java)
    } catch (_: JsonSyntaxException) {
        null
    }
    return apiError?.error ?: apiError?.message ?: raw
}
