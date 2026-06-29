package com.deepworktracker.data.remote.auth

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthSessionRepositoryImplTest {

    private val tokenStore = FakeTokenStore()
    private val coordinator = mockk<RefreshTokenCoordinator>()
    private lateinit var repository: AuthSessionRepositoryImpl

    @Before
    fun setUp() {
        repository = AuthSessionRepositoryImpl(tokenStore, coordinator)
    }

    @Test
    fun bootstrap_withoutRefreshToken_setsUnauthenticated() = runTest {
        tokenStore.refresh = null

        repository.bootstrap()

        assertEquals(AuthSessionState.Unauthenticated, repository.sessionState.value)
    }

    @Test
    fun bootstrap_withSuccessfulRefresh_setsAuthenticated() = runTest {
        tokenStore.refresh = "refresh-1"
        coEvery { coordinator.refreshAccessToken(null) } returns
            RefreshResult.Success("access-1")

        repository.bootstrap()

        assertEquals(AuthSessionState.Authenticated, repository.sessionState.value)
    }

    @Test
    fun bootstrap_withInvalidRefresh_clearsTokensAndSetsSessionExpired() = runTest {
        tokenStore.refresh = "refresh-1"
        coEvery { coordinator.refreshAccessToken(null) } returns
            RefreshResult.InvalidRefreshToken

        repository.bootstrap()

        assertTrue(tokenStore.clearCalled)
        assertEquals(
            AuthSessionState.SessionExpired(SessionExpiredReason.RefreshRevoked),
            repository.sessionState.value,
        )
    }

    @Test
    fun bootstrap_withNetworkError_setsBootstrapFailed() = runTest {
        tokenStore.refresh = "refresh-1"
        coEvery { coordinator.refreshAccessToken(null) } returns RefreshResult.NetworkError

        repository.bootstrap()

        assertEquals(
            AuthSessionState.BootstrapFailed(BootstrapError.NoInternet),
            repository.sessionState.value,
        )
        assertTrue(!tokenStore.clearCalled)
    }

    @Test
    fun logout_clearsTokensAndSetsUnauthenticated() = runTest {
        tokenStore.access = "access-1"
        tokenStore.refresh = "refresh-1"

        repository.logout()

        assertTrue(tokenStore.clearCalled)
        assertEquals(AuthSessionState.Unauthenticated, repository.sessionState.value)
    }

    @Test
    fun onLoginSuccess_setsAuthenticated() {
        repository.onLoginSuccess()

        assertEquals(AuthSessionState.Authenticated, repository.sessionState.value)
    }
}
