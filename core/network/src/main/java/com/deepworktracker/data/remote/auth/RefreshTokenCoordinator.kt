package com.deepworktracker.data.remote.auth

interface RefreshTokenCoordinator {
    /**
     * @param previousAccessToken token that produced a 401, or null for cold-start bootstrap.
     */
    suspend fun refreshAccessToken(previousAccessToken: String?): RefreshResult
}
