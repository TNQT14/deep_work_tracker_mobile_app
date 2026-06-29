package com.deepworktracker.data.remote.auth

sealed interface RefreshResult {
    data class Success(val accessToken: String) : RefreshResult
    data object NoRefreshToken : RefreshResult
    data object InvalidRefreshToken : RefreshResult
    data object NetworkError : RefreshResult
}
