package com.deepworktracker.session.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.InterruptionRepository
import com.deepworktracker.session.domain.usecase.GetSessionByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] [UDF]
 * Read-only end-session summary (roadmap #1, M1.3). Fetches the ended [FocusSession] and its
 * interruptions independently by [sessionId] rather than trusting [FocusSession.interruptions]
 * (that field is never persisted by [com.deepworktracker.data.mapper.SessionMapper] — it only
 * carries data in the object returned in-memory by EndSessionUseCase), so this screen shows
 * correct data even after a process death / rotation forces a fresh DB read.
 */
@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    private val interruptionRepository: InterruptionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: String = savedStateHandle.get<String>("sessionId").orEmpty()

    private val _uiState = MutableStateFlow(SessionSummaryUiState())
    val uiState: StateFlow<SessionSummaryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val session = getSessionByIdUseCase(sessionId)
            val interruptions = interruptionRepository.getInterruptionsBySession(sessionId).first()
            _uiState.update {
                it.copy(session = session, interruptions = interruptions, isLoading = false)
            }
        }
    }
}
