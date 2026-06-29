package com.deepworktracker.data.remote.token

import com.deepworktracker.data.remote.SecureRefreshTokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HybridTokenStore @Inject constructor(
    private val secureRefreshTokenStore: SecureRefreshTokenStore,
): TokenStore {
    @Volatile
    private var accessToken: String? = null
    override fun getAccessToken(): String? = accessToken
    override fun getRefreshToken(): String? = secureRefreshTokenStore.read()
    override fun setTokens(access: String, refresh: String?) {
        accessToken = access
        if(refresh != null){
            secureRefreshTokenStore.write(refresh)
        }
    }

    override fun clear() {
        accessToken = null
        secureRefreshTokenStore.clear()
    }

}