package com.deepworktracker.data.remote.network

import com.deepworktracker.data.remote.api.AuthApi
import com.deepworktracker.data.repository.AuthRepository
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class AuthRepositoryTestHarness {

    val server = MockWebServer()
    val gson = Gson()
    lateinit var repository: AuthRepository
        private set

    fun start() {
        server.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        repository = AuthRepository(retrofit.create(AuthApi::class.java), gson)
    }

    fun shutdown() {
        server.shutdown()
    }

    fun enqueue(response: MockResponse) {
        server.enqueue(response)
    }

    fun takeRequest(): RecordedRequest = server.takeRequest()
}
