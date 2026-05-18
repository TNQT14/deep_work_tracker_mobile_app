package com.deepworktracker.data.remote.network

import com.deepworktracker.data.remote.api.AuthApi
import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException

class AuthNetworkingTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: AuthRepository
    private lateinit var gson: Gson

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        gson = Gson()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(AuthApi::class.java)
        repository = AuthRepository(api, gson)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun login200_mapsToSuccess() = runTest {
        server.enqueue(
            MockResponse().setBody("{\"access_token\":\"tok\"}"),
        )
        val result = repository.login(LoginRequest("a@b.com", "secret"))
        assertTrue(result is NetworkResult.Success)
        assertEquals("tok", (result as NetworkResult.Success).data.accessToken)
    }

    @Test
    fun login401_mapsToUnauthorized() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("{\"message\":\"nope\"}"),
        )
        val result = repository.login(LoginRequest("a@b.com", "secret"))
        assertTrue(result is NetworkResult.Unauthorized)
    }

    @Test
    fun login500_mapsToServerError() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        val result = repository.login(LoginRequest("a@b.com", "secret"))
        assertTrue(result is NetworkResult.ServerError)
    }

    @Test
    fun loginMalformedJson_mapsToParseError() = runTest {
        server.enqueue(MockResponse().setBody("not json at all"))
        val result = repository.login(LoginRequest("a@b.com", "secret"))
        assertTrue(result is NetworkResult.ParseError)
    }

    @Test
    fun safeApiCall_unknownHost_mapsToNoInternet() = runTest {
        val result = safeApiCall<String>(gson) { throw UnknownHostException("offline") }
        assertTrue(result is NetworkResult.NoInternet)
    }

    @Test
    fun safeApiCall_jsonSyntax_mapsToParseError() = runTest {
        val result = safeApiCall<String>(gson) { throw JsonSyntaxException("bad") }
        assertTrue(result is NetworkResult.ParseError)
    }
}
