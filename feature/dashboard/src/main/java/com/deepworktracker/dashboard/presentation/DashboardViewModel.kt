package com.deepworktracker.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.dashboard.domain.usecase.GetAllSessionUseCase
import com.deepworktracker.dashboard.domain.usecase.GetRecentSessionsUseCase
import com.deepworktracker.dashboard.domain.usecase.GetTodayStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTodayStatsUseCase: GetTodayStatsUseCase,
    private val getRecentSessionsUseCase: GetRecentSessionsUseCase,
    private val getAllSessionUseCase: GetAllSessionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Load today's stats
            when (val statsResult = getTodayStatsUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(todayStats = statsResult.data) }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = statsResult.exception,
                            isLoading = false
                        )
                    }
                    return@launch
                }
            }
            
            // Load recent sessions
            when (val sessionsResult = getRecentSessionsUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            recentSessions = sessionsResult.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            error = sessionsResult.exception,
                            isLoading = false
                        )
                    }
                }
            }

            when(val allSessionResult = getAllSessionUseCase()){
                is Result.Success ->{
                 _uiState.update {
                     it.copy(
                         allSessions = allSessionResult.data,
                         isLoading = false
                     )
                 }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            error = allSessionResult.exception,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
