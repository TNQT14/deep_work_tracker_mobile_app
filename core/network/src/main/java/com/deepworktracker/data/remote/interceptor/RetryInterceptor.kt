package com.deepworktracker.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetryInterceptor(
    private val maxAttempts: Int = 3,
    private val initialBackoffMs: Long = TimeUnit.SECONDS.toMillis(1),
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var backoff = initialBackoffMs
        var attempt = 0

        while (attempt < maxAttempts) {
            try {
                val response = chain.proceed(chain.request())
                when {
                    response.isSuccessful -> return response
                    response.code in 500..599 -> {
                        val shouldRetry = attempt < maxAttempts - 1
                        if (!shouldRetry) {
                            return response
                        }
                        response.closeQuietly()
                        sleepQuietly(backoff)
                        backoff *= 2
                        attempt++
                    }
                    else -> return response
                }
            } catch (e: IOException) {
                val shouldRetry = attempt < maxAttempts - 1
                if (!shouldRetry) {
                    throw e
                }
                sleepQuietly(backoff)
                backoff *= 2
                attempt++
            }
        }

        throw AssertionError("RetryInterceptor exhausted without returning a response")
    }

    private fun Response.closeQuietly() {
        try {
            close()
        } catch (_: Exception) {
        }
    }

    private fun sleepQuietly(ms: Long) {
        try {
            Thread.sleep(ms)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}
