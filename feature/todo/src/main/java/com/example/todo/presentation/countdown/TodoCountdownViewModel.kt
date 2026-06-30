package com.example.todo.presentation.countdown

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

private const val DEFAULT_MINUTES = 25

@HiltViewModel
class TodoCountdownViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val todoId: String = savedStateHandle.get<String>("todoId").orEmpty()

    private val _uiState = MutableStateFlow(TodoCountdownUiState(isLoading = true))
    val uiState: StateFlow<TodoCountdownUiState> = _uiState.asStateFlow()

    private val _finishedEvent = Channel<Unit>(Channel.BUFFERED)
    val finishedEvent = _finishedEvent.receiveAsFlow()

    private var timerJob: Job? = null
    private var activeSessionId: String? = null

    init {
        observeTodo()
    }

    private fun observeTodo() {
        viewModelScope.launch {
            todoRepository.observeAllTodo()
                .map { list -> list.firstOrNull { it.id == todoId } }
                .collectLatest { todo ->
                    val minutes = todo?.estimatedMinutes ?: DEFAULT_MINUTES
                    val totalSec = minutes * 60
                    _uiState.update {
                        it.copy(
                            todo = todo,
                            totalSeconds = totalSec,
                            remainingSeconds = totalSec,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun start() {
        val todo = _uiState.value.todo ?: return
        viewModelScope.launch {
            val existingActive = sessionRepository.observeActiveSession().first()
            if (existingActive == null) {
                val session = FocusSession(
                    id = UUID.randomUUID().toString(),
                    goal = todo.title,
                    category = todo.goal.takeIf { it.isNotBlank() },
                    startTime = Clock.System.now(),
                    endTime = null,
                    totalDuration = 0L,
                    focusedDuration = 0L,
                    tag = null,
                    note = null,
                )
                sessionRepository.saveSession(session)
                activeSessionId = session.id
            } else {
                activeSessionId = existingActive.id
            }

            if (todo.status == TodoStatus.TODO) {
                val updated = todo.copy(
                    status = TodoStatus.IN_PROGRESS,
                    updatedAt = Clock.System.now(),
                )
                todoRepository.updateTodo(updated)
            }

            _uiState.update { it.copy(isRunning = true, isPaused = false) }
            startTimer()
        }
    }

    fun pause() {
        timerJob?.cancel()
        _uiState.update { it.copy(isPaused = true, isRunning = false) }
    }

    fun resume() {
        _uiState.update { it.copy(isPaused = false, isRunning = true) }
        startTimer()
    }

    fun finish(markDone: Boolean) {
        timerJob?.cancel()
        viewModelScope.launch {
            endActiveSession()
            if (markDone) {
                val todo = _uiState.value.todo
                if (todo != null && todo.status != TodoStatus.DONE) {
                    val now = Clock.System.now()
                    todoRepository.updateTodo(
                        todo.copy(
                            status = TodoStatus.DONE,
                            completedAt = now,
                            updatedAt = now,
                        )
                    )
                }
            }
            _uiState.update { it.copy(isRunning = false, isPaused = false) }
            _finishedEvent.send(Unit)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.remainingSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            if (_uiState.value.remainingSeconds == 0 && _uiState.value.isRunning) {
                onCountdownFinished()
            }
        }
    }

    private suspend fun onCountdownFinished() {
        endActiveSession()
        _uiState.update { it.copy(isRunning = false, isFinished = true) }
    }

    private suspend fun endActiveSession() {
        val sid = activeSessionId ?: return
        val session = sessionRepository.getSessionById(sid) ?: return
        val now = Clock.System.now()
        val total = (now - session.startTime).inWholeMilliseconds
        sessionRepository.updateSession(
            session.copy(
                endTime = now,
                totalDuration = total,
                focusedDuration = total,
            )
        )
        activeSessionId = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
