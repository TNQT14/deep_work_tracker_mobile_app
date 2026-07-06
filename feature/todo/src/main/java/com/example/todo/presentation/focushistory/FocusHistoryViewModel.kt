package com.example.todo.presentation.focushistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * [ViewModel] [UDF]
 * Observes completed focus sessions for one todo, groups them by day, and computes totals.
 * State ↓ via uiState StateFlow; no events from the screen (read-only history).
 */
@HiltViewModel
class FocusHistoryViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    todoRepository: TodoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Type: String | Sample: "todo-uuid-123" from route todo/{todoId}/focus-history */
    private val todoId: String = savedStateHandle.get<String>("todoId").orEmpty()

    private val _uiState = MutableStateFlow(FocusHistoryUiState())
    val uiState: StateFlow<FocusHistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoRepository.observeAllTodo()
                .map { list -> list.firstOrNull { it.id == todoId }?.title.orEmpty() }
                .collect { title -> _uiState.update { it.copy(todoTitle = title) } }
        }

        viewModelScope.launch {
            sessionRepository.getSessionsByTodoId(todoId).collect { sessions ->
                val zone = TimeZone.currentSystemDefault()
                // Only finished sessions; DAO already sorts start_time DESC (latest first)
                val items = sessions.filter { it.endTime != null }.map { it.toItem() }
                val sections = items
                    .groupBy { it.startTime.toLocalDateTime(zone).date }
                    .entries
                    .sortedByDescending { it.key }
                    .map { (date, dayItems) -> FocusDaySection(date, dayItems) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sections = sections,
                        totalFocusedMinutes = items.sumOf { i -> i.focusedMinutes },
                        sessionCount = items.size,
                        totalCycles = items.sumOf { i -> i.cycles },
                    )
                }
            }
        }
    }

    private fun FocusSession.toItem() = FocusSessionItem(
        id = id,
        startTime = startTime,
        endTime = requireNotNull(endTime),
        focusedMinutes = actualFocusedMinutes,
        cycles = cycles,
        repeat = repeat,
        breakMinutes = breakMinutes,
    )
}
