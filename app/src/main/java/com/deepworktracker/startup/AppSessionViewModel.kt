package com.deepworktracker.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.data.remote.auth.AuthSessionRepository
import com.deepworktracker.data.remote.auth.AuthSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the launch gate. Runs [AuthSessionRepository.bootstrap] once on cold start
 * (off the main thread, since it touches encrypted storage + network) and exposes
 * the resulting [AuthSessionState] for the root composable to route on.
 */
@HiltViewModel
class AppSessionViewModel @Inject constructor(
    private val authSessionRepository: AuthSessionRepository,
) : ViewModel() {

    val sessionState: StateFlow<AuthSessionState> = authSessionRepository.sessionState

    init {
        bootstrap()
    }

    fun bootstrap() {
        viewModelScope.launch(Dispatchers.IO) {
            authSessionRepository.bootstrap()
        }
    }

    /** From the bootstrap-failed screen: give up restoring and go to login. */
    fun continueToLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            authSessionRepository.logout()
        }
    }
}
