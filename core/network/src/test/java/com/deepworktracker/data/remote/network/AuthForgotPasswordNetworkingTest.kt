package com.deepworktracker.data.remote.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthForgotPasswordNetworkingTest {

    private val harness = AuthRepositoryTestHarness()

    companion object {
        private const val TEST_EMAIL = "user@example.com"
        private const val ENVELOPE_OK =
            """{"success":true,"data":{"message":"email verified"}}"""
    }

    @Before
    fun setUp() = harness.start()

    @After
    fun tearDown() = harness.shutdown()

    // --- verify email ---

    @Test
    fun verifyEmail200_mapsToSuccess() = runTest {
        harness.enqueue(MockResponse().setBody(ENVELOPE_OK))
        val result = harness.repository.verifyForgotPasswordEmail(TEST_EMAIL)
        assertTrue(result is NetworkResult.Success)
        val body = (result as NetworkResult.Success).data
        assertEquals(true, body.success)
        assertEquals("email verified", body.data?.message)
    }

    @Test
    fun verifyEmail401_mapsToUnauthorized() = runTest {
        harness.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"message":"invalid email"}"""),
        )
        val result = harness.repository.verifyForgotPasswordEmail(TEST_EMAIL)
        assertTrue(result is NetworkResult.Unauthorized)
    }

    @Test
    fun verifyEmail500_mapsToServerError() = runTest {
        harness.enqueue(MockResponse().setResponseCode(500))
        val result = harness.repository.verifyForgotPasswordEmail(TEST_EMAIL)
        assertTrue(result is NetworkResult.ServerError)
    }

    @Test
    fun verifyEmailMalformedJson_mapsToParseError() = runTest {
        harness.enqueue(MockResponse().setBody("not json at all"))
        val result = harness.repository.verifyForgotPasswordEmail(TEST_EMAIL)
        assertTrue(result is NetworkResult.ParseError)
    }

    @Test
    fun verifyEmail_sendsPostWithEmailInBody() = runTest {
        harness.enqueue(MockResponse().setBody(ENVELOPE_OK))
        harness.repository.verifyForgotPasswordEmail(TEST_EMAIL)
        val request = harness.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/auth/forgot-password/verify-email", request.path)
        assertTrue(request.body.readUtf8().contains(TEST_EMAIL))
    }

    // --- reset password ---

    @Test
    fun resetPassword200_mapsToSuccess() = runTest {
        harness.enqueue(
            MockResponse().setBody(
                """{"success":true,"data":{"message":"password reset"}}""",
            ),
        )
        val result = harness.repository.resetForgotPassword(TEST_EMAIL, "newSecret99")
        assertTrue(result is NetworkResult.Success)
        val body = (result as NetworkResult.Success).data
        assertEquals(true, body.success)
        assertEquals("password reset", body.data?.message)
    }

    @Test
    fun resetPassword401_mapsToUnauthorized() = runTest {
        harness.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"message":"invalid token"}"""),
        )
        val result = harness.repository.resetForgotPassword(TEST_EMAIL, "newSecret99")
        assertTrue(result is NetworkResult.Unauthorized)
    }

    @Test
    fun resetPassword500_mapsToServerError() = runTest {
        harness.enqueue(MockResponse().setResponseCode(500))
        val result = harness.repository.resetForgotPassword(TEST_EMAIL, "newSecret99")
        assertTrue(result is NetworkResult.ServerError)
    }

    @Test
    fun resetPasswordMalformedJson_mapsToParseError() = runTest {
        harness.enqueue(MockResponse().setBody("not json at all"))
        val result = harness.repository.resetForgotPassword(TEST_EMAIL, "newSecret99")
        assertTrue(result is NetworkResult.ParseError)
    }

    @Test
    fun resetPassword_sendsPostWithEmailAndPasswordInBody() = runTest {
        harness.enqueue(
            MockResponse().setBody("""{"success":true,"data":{"message":"ok"}}"""),
        )
        harness.repository.resetForgotPassword(TEST_EMAIL, "newSecret99")
        val request = harness.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/v1/auth/forgot-password/reset", request.path)
        val body = request.body.readUtf8()
        assertTrue(body.contains(TEST_EMAIL))
        assertTrue(body.contains("newSecret99"))
    }
}
