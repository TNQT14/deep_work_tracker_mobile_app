package com.deepworktracker.data.remote.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthLogoutNetworkingTest {

    private val harness = AuthRepositoryTestHarness()

    @Before
    fun setUp() = harness.start()

    @After
    fun tearDown() = harness.shutdown()

    @Test
    fun logout200_mapsToSuccess() = runTest {
        harness.enqueue(
            MockResponse().setBody(
                """{"success":true,"data":{"message":"logged out successfully"}}""",
            ),
        )
        val result = harness.repository.logout("access-token-123")
        assertTrue(result is NetworkResult.Success)
        val envelope = (result as NetworkResult.Success).data
        assertTrue(envelope.success)
        assertEquals("logged out successfully", envelope.data?.message)
    }

    @Test
    fun logout_sendsAccessTokenInBody() = runTest {
        harness.enqueue(
            MockResponse().setBody(
                """{"success":true,"data":{"message":"logged out successfully"}}""",
            ),
        )
        harness.repository.logout("my-access-token")
        val request = harness.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/auth/logout", request.path)
        assertTrue(request.body.readUtf8().contains("my-access-token"))
    }

    @Test
    fun logout401_mapsToUnauthorized() = runTest {
        harness.enqueue(MockResponse().setResponseCode(401))
        val result = harness.repository.logout("expired-token")
        assertTrue(result is NetworkResult.Unauthorized)
    }

    @Test
    fun logoutEnvelopeFalse_mapsToSuccessWithFalseFlag() = runTest {
        harness.enqueue(
            MockResponse().setBody(
                """{"success":false,"data":{"message":"invalid token"}}""",
            ),
        )
        val result = harness.repository.logout("bad-token")
        assertTrue(result is NetworkResult.Success)
        assertEquals(false, (result as NetworkResult.Success).data.success)
    }
}
