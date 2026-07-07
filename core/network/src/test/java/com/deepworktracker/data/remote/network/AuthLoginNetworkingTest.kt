package com.deepworktracker.data.remote.network

import com.deepworktracker.data.remote.model.request.LoginRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthLoginNetworkingTest {

    private val harness = AuthRepositoryTestHarness()

    @Before
    fun setUp() = harness.start()

    @After
    fun tearDown() = harness.shutdown()

    @Test
    fun login200_mapsToSuccess() = runTest {
        // Real backend shape: {"success": true, "data": {"access_token": ..., "refresh_token": ...}}
        harness.enqueue(
            MockResponse().setBody(
                """{"success":true,"data":{"access_token":"tok","refresh_token":"refresh-tok"}}""",
            ),
        )
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("tok", data.accessToken)
        assertEquals("refresh-tok", data.refreshToken)
    }

    @Test
    fun login200WithFlatBody_mapsToParseError() = runTest {
        // Regression guard: a flat (unwrapped) body must NOT silently parse into a Success
        // with null tokens — this was the bug that skipped persisting the refresh token.
        harness.enqueue(MockResponse().setBody("""{"access_token":"tok"}"""))
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.ParseError)
    }

    @Test
    fun login200WithSuccessFalse_mapsToParseError() = runTest {
        harness.enqueue(MockResponse().setBody("""{"success":false,"data":null}"""))
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.ParseError)
    }

    @Test
    fun login401_mapsToUnauthorized() = runTest {
        harness.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"message":"nope"}"""),
        )
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.Unauthorized)
    }

    @Test
    fun login500_mapsToServerError() = runTest {
        harness.enqueue(MockResponse().setResponseCode(500))
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.ServerError)
    }

    @Test
    fun loginMalformedJson_mapsToParseError() = runTest {
        harness.enqueue(MockResponse().setBody("not json at all"))
        val result = harness.repository.login(LoginRequest("user@example.com", "secret"))
        assertTrue(result is NetworkResult.ParseError)
    }
}
