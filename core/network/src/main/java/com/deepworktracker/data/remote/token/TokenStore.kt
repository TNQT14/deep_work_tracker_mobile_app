package com.deepworktracker.data.remote.token

interface TokenStore {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun setTokens(access: String, refresh: String?)
    fun clear()
}
