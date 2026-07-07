package com.deepworktracker.data.remote.auth

import com.deepworktracker.data.remote.api.TokenRefreshApi
import com.deepworktracker.data.remote.model.response.ApiEnvelope
import com.deepworktracker.data.remote.model.response.AuthResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import javax.inject.Provider

class RefreshTokenCoordinatorImplTest {

    private val tokenStore = FakeTokenStore()
    private val tokenRefreshApi = mockk<TokenRefreshApi>()
    private val authSessionRepository = mockk<AuthSessionRepository>(relaxed = true)
    private val authSessionRepositoryProvider = Provider { authSessionRepository }
    private lateinit var coordinator: RefreshTokenCoordinatorImpl

    @Before
    fun setUp() {
        coordinator = RefreshTokenCoordinatorImpl(
            tokenStore,
            tokenRefreshApi,
            authSessionRepositoryProvider,
        )
    }

    @Test
    fun refreshAccessToken_withoutRefreshToken_returnsNoRefreshToken() = runTest {
        tokenStore.refresh = null

        val result = coordinator.refreshAccessToken(null)

        assertEquals(RefreshResult.NoRefreshToken, result)
        coVerify(exactly = 0) { tokenRefreshApi.refresh(any()) }
    }

    @Test
    fun refreshAccessToken_withSuccessfulResponse_persistsTokens() = runTest {
        tokenStore.refresh = "refresh-1"
        val response = mockk<Response<ApiEnvelope<AuthResponse>>>()
        every { response.isSuccessful } returns true
        every { response.body() } returns ApiEnvelope(
            success = true,
            data = AuthResponse(accessToken = "new-access", refreshToken = "new-refresh"),
        )
        coEvery { tokenRefreshApi.refresh(any()) } returns response

        val result = coordinator.refreshAccessToken(null)

        assertEquals(RefreshResult.Success("new-access"), result)
        assertEquals("new-access", tokenStore.access)
        assertEquals("new-refresh", tokenStore.refresh)
    }

    @Test
    fun refreshAccessToken_with401_returnsInvalidRefreshToken() = runTest {
        tokenStore.refresh = "refresh-1"
        val response = mockk<Response<ApiEnvelope<AuthResponse>>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns 401
        coEvery { tokenRefreshApi.refresh(any()) } returns response

        val result = coordinator.refreshAccessToken(null)

        assertEquals(RefreshResult.InvalidRefreshToken, result)
        coVerify(exactly = 1) { authSessionRepository.onRefreshTokenRevoked() }
    }

    @Test
    fun refreshAccessToken_with403_returnsInvalidRefreshToken() = runTest {
        tokenStore.refresh = "refresh-1"
        val response = mockk<Response<ApiEnvelope<AuthResponse>>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns 403
        coEvery { tokenRefreshApi.refresh(any()) } returns response

        val result = coordinator.refreshAccessToken(null)

        assertEquals(RefreshResult.InvalidRefreshToken, result)
        coVerify(exactly = 1) { authSessionRepository.onRefreshTokenRevoked() }
    }

    @Test
    fun refreshAccessToken_reusesAlreadyRefreshedAccessToken() = runTest {
        tokenStore.access = "fresh-access"
        tokenStore.refresh = "refresh-1"

        val result = coordinator.refreshAccessToken("stale-access")

        assertEquals(RefreshResult.Success("fresh-access"), result)
        coVerify(exactly = 0) { tokenRefreshApi.refresh(any()) }
    }

    @Test
    fun refreshAccessToken_concurrent401Handlers_singleApiCall() = runTest {
        tokenStore.access = "stale-access"
        tokenStore.refresh = "refresh-1"
        val response = mockk<Response<ApiEnvelope<AuthResponse>>>()
        every { response.isSuccessful } returns true
        every { response.body() } returns ApiEnvelope(
            success = true,
            data = AuthResponse(accessToken = "new-access"),
        )
        coEvery { tokenRefreshApi.refresh(any()) } coAnswers {
            kotlinx.coroutines.delay(50)
            response
        }

        val first = async { coordinator.refreshAccessToken("stale-access") }
        val second = async { coordinator.refreshAccessToken("stale-access") }

        assertTrue(first.await() is RefreshResult.Success)
        assertTrue(second.await() is RefreshResult.Success)
        coVerify(exactly = 1) { tokenRefreshApi.refresh(any()) }
    }
}
