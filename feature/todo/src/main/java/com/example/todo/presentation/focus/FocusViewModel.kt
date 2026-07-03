package com.example.todo.presentation.focus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusConfig
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.TodoRepository
import com.example.todo.notification.FocusNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

/**
 * [ViewModel] [UDF]
 * Owns focus session timer, persistence, and todo updates.
 * Events ↑ from FocusScreen / PreFocusConfigBottomSheet; state ↓ via uiState StateFlow.
 * One-off navigation/sound effects use Channel → receiveAsFlow in UI.
 */
@HiltViewModel
class FocusViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val sessionRepository: SessionRepository,
    private val notificationHelper: FocusNotificationHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * [Nav]
     * Type: String | Sample: "todo-uuid-123" from route todo/{todoId}/focus
     */
    private val todoId: String = savedStateHandle.get<String>("todoId").orEmpty()

    private val _uiState = MutableStateFlow(FocusUiState(isLoading = true))

    /**
     * [ViewModel] [UDF]
     * Type: StateFlow<FocusUiState>
     * Sample: FocusUiState(todo=Todo(...), isRunning=true, focusRemainingSeconds=1200, ...)
     */
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    /**
     * [Effect]
     * Emitted once after endSession() persists data; UI pops back via doneEvent.collect
     */
    private val _doneEvent = Channel<Unit>(Channel.BUFFERED)
    val doneEvent = _doneEvent.receiveAsFlow()

    /**
     * [Effect]
     * Emitted when focus timer reaches 0 and repeat=false; UI shows EndSessionDialog
     */
    private val _timerFinishedEvent = Channel<Unit>(Channel.BUFFERED)
    val timerFinishedEvent = _timerFinishedEvent.receiveAsFlow()

    /**
     * [Effect]
     * Emitted when a Pomodoro cycle completes with repeat=true and alertMode=NOTIFY; UI plays sound
     */
    private val _cycleCompletedEvent = Channel<Unit>(Channel.BUFFERED)
    val cycleCompletedEvent = _cycleCompletedEvent.receiveAsFlow()

    private var timerJob: Job? = null

    /**
     * Type: String? | Sample: UUID of FocusSession created at startWithConfig()
     */
    private var activeSessionId: String? = null

    init {
        loadTodo()
    }

    /**
     * [ViewModel] [UDF]
     * Input: none (uses todoId from SavedStateHandle)
     * Process: observeAllTodo() → find matching todo → keep collecting for live updates
     * Output: uiState.todo set; isLoading=false
     */
    private fun loadTodo() {
        viewModelScope.launch {
            val todoFlow = todoRepository.observeAllTodo()
                .map { list -> list.firstOrNull { it.id == todoId } }
            val todo = todoFlow.first()
            _uiState.update { it.copy(todo = todo, isLoading = false) }
            todoFlow.collect { updated -> _uiState.update { it.copy(todo = updated) } }
        }
    }

    /**
     * [ViewModel] [UDF]
     * Input: config — e.g. FocusConfig(focusMinutes=25, breakMinutes=5, repeat=false, alertMode=NOTIFY)
     * Process: store config → create/reuse FocusSession → start 1s timer coroutine
     * Output: uiState.config set, focusRemainingSeconds initialized, isRunning=true
     */
    fun startWithConfig(config: FocusConfig) {
        val todo = _uiState.value.todo ?: return
        val totalSec = config.focusMinutes * 60
        _uiState.update {
            it.copy(
                config = config,
                focusRemainingSeconds = totalSec,
                isRunning = false,
                isPaused = false,
            )
        }
        viewModelScope.launch {
            val existing = sessionRepository.observeActiveSession().first()
            if (existing == null) {
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
                    todoId = todoId,
                    focusMinutes = config.focusMinutes,
                    breakMinutes = config.breakMinutes,
                    repeat = config.repeat,
                    alertMode = config.alertMode,
                )
                sessionRepository.saveSession(session)
                activeSessionId = session.id
            } else {
                activeSessionId = existing.id
            }
            resumeTimer()
        }
    }

    /**
     * [ViewModel]
     * Input: none
     * Process: cancel timer job, dismiss notification
     * Output: isRunning=false, isPaused=true
     */
    fun pause() {
        timerJob?.cancel()
        notificationHelper.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
    }

    /**
     * [ViewModel]
     * Input: none
     * Process: restart 1-second countdown coroutine
     * Output: isRunning=true, isPaused=false
     */
    fun resume() {
        resumeTimer()
    }

    /**
     * [ViewModel] [UDF]
     * Input: markDone — e.g. true when user checks "Mark task as done" in EndSessionDialog
     * Process: stop timer → deduct remainingMinutes on todo → close FocusSession → emit doneEvent
     * Output: todo updated in repository; session endTime set; UI navigates back
     */
    fun endSession(markDone: Boolean) {
        timerJob?.cancel()
        notificationHelper.cancel()
        viewModelScope.launch {
            val state = _uiState.value
            val todo = state.todo
            val now = Clock.System.now()
            val actualMinutes = state.actualFocusedMinutes

            if (todo != null) {
                val currentRemaining = todo.remainingMinutes ?: todo.estimatedMinutes
                val newRemaining = if (currentRemaining != null) {
                    maxOf(0, currentRemaining - actualMinutes)
                } else null

                val newStatus = when {
                    markDone -> TodoStatus.DONE
                    todo.status == TodoStatus.TODO -> TodoStatus.IN_PROGRESS
                    else -> todo.status
                }
                todoRepository.updateTodo(
                    todo.copy(
                        status = newStatus,
                        remainingMinutes = newRemaining,
                        completedAt = if (markDone) now else todo.completedAt,
                        updatedAt = now,
                    )
                )
            }

            val sid = activeSessionId
            if (sid != null) {
                val session = sessionRepository.getSessionById(sid)
                if (session != null) {
                    val elapsed = (now - session.startTime).inWholeMilliseconds
                    sessionRepository.updateSession(
                        session.copy(
                            endTime = now,
                            totalDuration = elapsed,
                            focusedDuration = (state.actualFocusedSeconds * 1000L),
                            actualFocusedMinutes = actualMinutes,
                            cycles = state.cycles,
                        )
                    )
                }
            }

            _uiState.update { it.copy(isRunning = false, isPaused = false) }
            _doneEvent.send(Unit)
        }
    }

    /**
     * [ViewModel]
     * Input: none (reads current uiState)
     * Process: launch coroutine that ticks every 1s; update notification every 5s
     * Output: focusRemainingSeconds decrements; actualFocusedSeconds increments;
     *         on zero → onFocusTimerFinished()
     */
    private fun resumeTimer() {
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.focusRemainingSeconds > 0) {
                delay(1000)
                val newRemaining = _uiState.value.focusRemainingSeconds - 1
                _uiState.update {
                    it.copy(
                        focusRemainingSeconds = newRemaining,
                        actualFocusedSeconds = it.actualFocusedSeconds + 1,
                    )
                }
                if (newRemaining % 5 == 0) {
                    val s = _uiState.value
                    notificationHelper.showFocus(s.todo?.title, newRemaining, s.cycles)
                }
            }
            if (_uiState.value.focusRemainingSeconds == 0 && _uiState.value.isRunning) {
                onFocusTimerFinished()
            }
        }
    }

    /**
     * [ViewModel]
     * Input: none
     * Process: reset focusRemainingSeconds to full phase duration and restart timer
     * Output: EndSessionDialog dismissed path — user chose "Keep focusing"
     */
    fun continueSession() {
        val config = _uiState.value.config ?: return
        val totalSec = config.focusMinutes * 60
        _uiState.update { it.copy(focusRemainingSeconds = totalSec) }
        resumeTimer()
    }

    /**
     * [ViewModel] [Effect]
     * Input: none (called when timer hits 0)
     * Process: increment cycles; if repeat → auto-restart phase (+ optional sound event);
     *          else emit timerFinishedEvent for EndSessionDialog
     * Output: cycles+1; either new timer or Channel event to UI
     */
    private suspend fun onFocusTimerFinished() {
        timerJob?.cancel()
        notificationHelper.cancel()
        val config = _uiState.value.config
        _uiState.update { it.copy(isRunning = false, cycles = it.cycles + 1) }

        if (config?.repeat == true) {
            if (config.alertMode == AlertMode.NOTIFY) {
                _cycleCompletedEvent.send(Unit)
            }
            val totalSec = config.focusMinutes * 60
            _uiState.update { it.copy(focusRemainingSeconds = totalSec) }
            resumeTimer()
        } else {
            _timerFinishedEvent.send(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        notificationHelper.cancel()
    }
}
