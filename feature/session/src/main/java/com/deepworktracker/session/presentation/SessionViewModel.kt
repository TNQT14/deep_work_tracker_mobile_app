package com.deepworktracker.session.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.session.domain.usecase.EndSessionUseCase
import com.deepworktracker.session.domain.usecase.GetActiveSessionUseCase
import com.deepworktracker.session.domain.usecase.GetRecentGoalsUseCase
import com.deepworktracker.session.domain.usecase.StartSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val getRecentGoalsUseCase: GetRecentGoalsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    
    init {
        observeActiveSession()
        loadRecentGoal()
    }
    
    fun startSession(goal: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = startSessionUseCase(goal)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            session = result.data,
                            isTracking = true,
                            isLoading = false
                        )
                    }
                    startTimer()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun loadRecentGoal(){
        viewModelScope.launch {
            val recentGoals = getRecentGoalsUseCase()
            _uiState.update { it.copy(recentSession = recentGoals) }
    }}
    
    fun endSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = endSessionUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            session = result.data,
                            isTracking = false,
                            isLoading = false
                        )
                    }
                    stopTimer()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = result.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    private fun observeActiveSession() {
        viewModelScope.launch {
            getActiveSessionUseCase().collect { session ->
                _uiState.update { it.copy(session = session) }
                if (session != null && !_uiState.value.isTracking) {
                    _uiState.update { it.copy(isTracking = true) }
                    startTimer()
                } else if (session == null) {
                    _uiState.update { it.copy(isTracking = false) }
                    stopTimer()
                }
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {
                val session = _uiState.value.session
                if (session != null) {
                    val elapsed = Clock.System.now() - session.startTime
                    _uiState.update { it.copy(elapsedTime = elapsed) }
                }
                delay(1000) // Update every second
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
