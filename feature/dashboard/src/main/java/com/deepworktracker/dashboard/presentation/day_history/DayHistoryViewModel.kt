package com.deepworktracker.dashboard.presentation.day_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val date: LocalDate = LocalDate.parse(savedStateHandle.get<String>(ARG_DATE).orEmpty())

    private val _uiState = MutableStateFlow(DayHistoryUiState(date = date))
    val uiState: StateFlow<DayHistoryUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            try {
                sessionRepository.getSessionsByDate(date).collect { all ->
                    _uiState.update {
                        it.copy(
                            sessions = all.filter { s -> !s.isActive }
                                .sortedBy { s -> s.startTime },
                            activeSession = all.firstOrNull { s -> s.isActive },
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load sessions"
                    )
                }
            }
        }
    }

    companion object {
        const val ARG_DATE = "date"
    }
}