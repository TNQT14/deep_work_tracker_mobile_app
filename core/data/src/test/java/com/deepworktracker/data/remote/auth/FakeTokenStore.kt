package com.deepworktracker.data.remote.auth

import com.deepworktracker.data.remote.token.TokenStore

internal class FakeTokenStore : TokenStore {
    var access: String? = null
    var refresh: String? = null
    var clearCalled = false

    override fun getAccessToken(): String? = access

    override fun getRefreshToken(): String? = refresh

    override fun setTokens(access: String, refresh: String?) {
        this.access = access
        if (refresh != null) {
            this.refresh = refresh
        }
    }

    override fun clear() {
        access = null
        refresh = null
        clearCalled = true
    }
}
