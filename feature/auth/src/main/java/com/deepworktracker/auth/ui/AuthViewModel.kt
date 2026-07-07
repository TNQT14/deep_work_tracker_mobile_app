package com.deepworktracker.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.data.remote.model.request.LoginRequest
import com.deepworktracker.data.remote.model.request.RegisterRequest
import com.deepworktracker.data.remote.model.response.AuthResponse
import com.deepworktracker.data.remote.network.NetworkResult
import com.deepworktracker.data.remote.auth.AuthSessionRepository
import com.deepworktracker.data.repository.AuthRepository
import com.deepworktracker.data.remote.token.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface AuthUiState<out T> {
    data object Idle : AuthUiState<Nothing>
    data object Loading : AuthUiState<Nothing>
    data class Success<T>(val data: T) : AuthUiState<T>
    data class Error(val message: String) : AuthUiState<Nothing>
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore,
    private val authSessionRepository: AuthSessionRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthUiState<AuthResponse>>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState<AuthResponse>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthUiState<AuthResponse>>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState<AuthResponse>> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading
            when (val result = authRepository.login(LoginRequest(email = email, password = password))) {
                is NetworkResult.Success -> {
                    persistTokensIfPresent(result.data)
                    _loginState.value = AuthUiState.Success(result.data)
                }

                is NetworkResult.HttpError -> {
                    _loginState.value = AuthUiState.Error(
                        result.message ?: "HTTP ${result.code}",
                    )
                }

                is NetworkResult.Unauthorized -> {
                    _loginState.value = AuthUiState.Error("Unauthorized")
                }

                is NetworkResult.NotFound -> {
                    _loginState.value = AuthUiState.Error("Not found")
                }

                is NetworkResult.ServerError -> {
                    _loginState.value = AuthUiState.Error("Server error")
                }

                is NetworkResult.NoInternet -> {
                    _loginState.value = AuthUiState.Error("No internet connection")
                }

                is NetworkResult.ParseError -> {
                    _loginState.value = AuthUiState.Error("Could not read server response")
                }

                is NetworkResult.NetworkError -> {
                    _loginState.value = AuthUiState.Error(
                        result.message ?: "Network error",
                    )
                }
            }
        }
    }

    fun register(email: String, fullName: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthUiState.Loading
            when (
                val result = authRepository.register(
                    RegisterRequest(
                        email = email,
                        fullName = fullName,
                        password = password,
                    ),
                )
            ) {
                is NetworkResult.Success -> {
                    persistTokensIfPresent(result.data)
                    _registerState.value = AuthUiState.Success(result.data)
                }

                is NetworkResult.HttpError -> {
                    _registerState.value = AuthUiState.Error(
                        result.message ?: "HTTP ${result.code}",
                    )
                }

                is NetworkResult.Unauthorized -> {
                    _registerState.value = AuthUiState.Error("Unauthorized")
                }

                is NetworkResult.NotFound -> {
                    _registerState.value = AuthUiState.Error("Not found")
                }

                is NetworkResult.ServerError -> {
                    _registerState.value = AuthUiState.Error("Server error")
                }

                is NetworkResult.NoInternet -> {
                    _registerState.value = AuthUiState.Error("No internet connection")
                }

                is NetworkResult.ParseError -> {
                    _registerState.value = AuthUiState.Error("Could not read server response")
                }

                is NetworkResult.NetworkError -> {
                    _registerState.value = AuthUiState.Error(
                        result.message ?: "Network error",
                    )
                }
            }
        }
    }

    fun consumeLoginSuccess() {
        _loginState.value = AuthUiState.Idle
    }

    fun consumeRegisterSuccess() {
        _registerState.value = AuthUiState.Idle
    }

    fun resetLoginError() {
        val v = _loginState.value
        if (v is AuthUiState.Error) {
            _loginState.value = AuthUiState.Idle
        }
    }

    fun resetRegisterError() {
        val v = _registerState.value
        if (v is AuthUiState.Error) {
            _registerState.value = AuthUiState.Idle
        }
    }

    private suspend fun persistTokensIfPresent(data: AuthResponse) {
        val access = data.accessToken ?: return
        // Encrypted-prefs write is synchronous (commit) → keep it off the main thread.
        withContext(Dispatchers.IO) {
            tokenStore.setTokens(access = access, refresh = data.refreshToken)
        }
        authSessionRepository.onLoginSuccess()
    }
}
