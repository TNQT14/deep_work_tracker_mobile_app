package com.deepworktracker.auth.ui.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.auth.ui.AuthUiState
import com.deepworktracker.data.remote.network.NetworkResult
import com.deepworktracker.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _verifyState = MutableStateFlow<AuthUiState<Unit>>(AuthUiState.Idle)
    val verifyState: StateFlow<AuthUiState<Unit>> = _verifyState.asStateFlow()
    private val _resetState = MutableStateFlow<AuthUiState<Unit>>(AuthUiState.Idle)
    val resetState: StateFlow<AuthUiState<Unit>> = _resetState.asStateFlow()
    private var verifiedEmail: String? = null
    fun verifyEmail(email: String) {
        val trimmed = email.trim()
        if (trimmed.isBlank()) {
            _verifyState.value = AuthUiState.Error("Email is required")
            return
        }

        viewModelScope.launch {
            _verifyState.value = AuthUiState.Loading
            when (val result = authRepository.verifyForgotPasswordEmail(trimmed)) {
                is NetworkResult.Success -> {
                    val envelope = result.data
                    if (envelope.success) {
                        verifiedEmail = trimmed
                        _verifyState.value = AuthUiState.Success(Unit)
                    } else {
                        _verifyState.value = AuthUiState.Error(
                            envelope.data?.message ?: "Email verification failed",
                        )
                    }
                }

                else -> _verifyState.value = mapNetworkError(result)
            }
        }
    }

    fun resetPassword(
        email: String,
        newPassword: String,
        confirmPassword: String,
    ) {
        val trimmedEmail = email.trim()
        val verified = verifiedEmail
        if (verified == null || verified != trimmedEmail) {
            _resetState.value = AuthUiState.Error("Please verify your email first")
            return
        }
        if (newPassword.isBlank()) {
            _resetState.value = AuthUiState.Error("Password is required")
            return
        }
        if (newPassword != confirmPassword) {
            _resetState.value = AuthUiState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _resetState.value = AuthUiState.Loading
            when (val result = authRepository.resetForgotPassword(trimmedEmail, newPassword)) {
                is NetworkResult.Success -> {
                    val envelope = result.data
                    if (envelope.success) {
                        verifiedEmail = null
                        _resetState.value = AuthUiState.Success(Unit)
                    } else {
                        _resetState.value = AuthUiState.Error(
                            envelope.data?.message ?: "Password reset failed",
                        )
                    }
                }

                else -> _resetState.value = mapNetworkError(result)
            }
        }
    }

    fun consumeVerifySuccess(){
        if(_verifyState.value is AuthUiState.Success){
            _verifyState.value = AuthUiState.Idle
        }
    }
    fun consumeResetSuccess() {
        if (_resetState.value is AuthUiState.Success) {
            _resetState.value = AuthUiState.Idle
        }
    }
    fun clearVerifyError() {
        if (_verifyState.value is AuthUiState.Error) {
            _verifyState.value = AuthUiState.Idle
        }
    }
    fun clearResetError() {
        if (_resetState.value is AuthUiState.Error) {
            _resetState.value = AuthUiState.Idle
        }
    }
    /** Gọi từ ResetRoute khi vào màn — gắn email từ navigation với bước verify */
    fun bindVerifiedEmailFromNavigation(email: String) {
        verifiedEmail = email.trim()
    }
    private fun mapNetworkError(result: NetworkResult<*>): AuthUiState.Error {
        val message = when (result) {
            is NetworkResult.HttpError -> result.message ?: "HTTP ${result.code}"
            is NetworkResult.Unauthorized -> "Unauthorized"
            is NetworkResult.NotFound -> "Not found"
            is NetworkResult.ServerError -> "Server error"
            is NetworkResult.NoInternet -> "No internet connection"
            is NetworkResult.ParseError -> "Could not read server response"
            is NetworkResult.NetworkError -> result.message ?: "Network error"
            is NetworkResult.Success -> "Unexpected success"
        }
        return AuthUiState.Error(message)
    }
}