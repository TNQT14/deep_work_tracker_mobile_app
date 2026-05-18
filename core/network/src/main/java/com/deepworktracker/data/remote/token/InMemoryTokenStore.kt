package com.deepworktracker.data.remote.token

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTokenStore @Inject constructor() : TokenStore {

    @Volatile
    private var accessToken: String? = null

    @Volatile
    private var refreshToken: String? = null

    override fun getAccessToken(): String? = accessToken

    override fun getRefreshToken(): String? = refreshToken

    override fun setTokens(access: String, refresh: String?) {
        accessToken = access
        if (refresh != null) {
            refreshToken = refresh
        }
    }

    override fun clear() {
        accessToken = null
        refreshToken = null
    }
}
