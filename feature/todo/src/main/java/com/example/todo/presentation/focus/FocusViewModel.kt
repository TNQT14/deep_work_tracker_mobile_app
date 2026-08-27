package com.example.todo.presentation.focus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.common.result.Result
import com.deepworktracker.domain.model.AlertMode
import com.deepworktracker.domain.model.FocusConfig
import com.deepworktracker.domain.model.FocusSession
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.Todo
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val observedTodoId = MutableStateFlow(savedStateHandle.get<String>("todoId").orEmpty())

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
                    todoId = observedTodoId.value,
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
            sessionServiceController.start()
            resumeTimer()
        }
    }

    /**
     * [ViewModel] [UDF]
     * Input: todo — another incomplete task chosen from the switch-task picker
     * Process: end the current FocusSession row and start a new one that shares sittingId;
     *          timer keeps running; observation retargets to [todo]
     * Output: activeSessionId + uiState.todo follow the new task
     */
    fun switchTask(todo: Todo) {
        if (todo.id == observedTodoId.value) return
        viewModelScope.launch {
            when (val result = switchTaskUseCase(todo.id, todo.title)) {
                is Result.Success -> {
                    activeSessionId = result.data.id
                    observedTodoId.value = todo.id
                    if (todo.status == TodoStatus.TODO) {
                        todoRepository.updateTodo(
                            todo.copy(status = TodoStatus.IN_PROGRESS, updatedAt = Clock.System.now())
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception) }
                }
            }
        }
    }

    /**
     * [ViewModel] [UDF]
     * Input: todo — another incomplete task chosen from the switch-task picker
     * Process: end the current FocusSession row and start a new one that shares sittingId;
     *          timer keeps running; observation retargets to [todo]
     * Output: activeSessionId + uiState.todo follow the new task
     */
    fun switchTask(todo: Todo) {
        if (todo.id == observedTodoId.value) return
        viewModelScope.launch {
            when (val result = switchTaskUseCase(todo.id, todo.title)) {
                is Result.Success -> {
                    activeSessionId = result.data.id
                    observedTodoId.value = todo.id
                    if (todo.status == TodoStatus.TODO) {
                        todoRepository.updateTodo(
                            todo.copy(status = TodoStatus.IN_PROGRESS, updatedAt = Clock.System.now())
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception) }
                }
            }
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
                when (val result = endSessionUseCase()) {
                    is Result.Success -> {
                        val timerMs = state.actualFocusedSeconds * 1000L
                        val focused = minOf(result.data.focusedDuration, timerMs).coerceAtLeast(0L)
                        sessionRepository.updateSession(
                            result.data.copy(
                                focusedDuration = focused,
                                actualFocusedMinutes = (focused / 60_000L).toInt(),
                                cycles = state.cycles,
                            )
                        )
                    }
                    is Result.Error -> {
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
                }
                sessionServiceController.stop()
            }

            notificationHelper.showCompleted(todo?.title, actualMinutes, state.cycles)

            _uiState.update { it.copy(isRunning = false, isPaused = false) }
            _doneEvent.send(Unit)
        }
    }

    /**
     * [ViewModel]
     * Input: none (reads current uiState)
     * Process: bump timerGeneration (UI re-keys progress sweep) → launch coroutine
     *          ticking every 1s; update notification every 5s
     * Output: focusRemainingSeconds decrements; actualFocusedSeconds increments;
     *         on zero → onFocusTimerFinished()
     */
    private fun resumeTimer() {
        _uiState.update {
            it.copy(isRunning = true, isPaused = false, timerGeneration = it.timerGeneration + 1)
        }
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
     * [ViewModel] [UDF]
     * Input: none — EndSessionDialog "Continue" path
     * Process: force phase back to FOCUS (dialog can open during BREAK), clear break timer,
     *          reset focusRemainingSeconds to config full duration → resumeTimer()
     * Output: fresh FOCUS countdown; timerGeneration bump restarts UI progress sweep
     */
    fun continueSession() {
        val config = _uiState.value.config ?: return
        val totalSec = config.focusMinutes * 60
        // phase reset covers "End session → Continue" chosen while on BREAK
        _uiState.update {
            it.copy(
                phase = FocusPhase.FOCUS,
                focusRemainingSeconds = totalSec,
                breakRemainingSeconds = 0,
            )
        }
        resumeTimer()
    }

    /**
     * [ViewModel] [UDF]
     * Input: none — "Back to focus" button during BREAK phase
     * Process: cancel break timer → roll unused breakRemainingSeconds into
     *          accumulatedBreakSeconds (in-memory only) → startNextFocusCycle()
     * Output: phase=FOCUS with full focus duration; accumulated break shown on next BREAK
     */
    fun skipBreak() {
        timerJob?.cancel()
        notificationHelper.cancel()
        val remaining = _uiState.value.breakRemainingSeconds
        _uiState.update {
            it.copy(
                accumulatedBreakSeconds = it.accumulatedBreakSeconds + remaining,
                breakRemainingSeconds = 0,
                isRunning = false,
            )
        }
        startNextFocusCycle()
    }

    /**
     * [ViewModel]
     * Input: none (requires config; no-op if user never configured)
     * Process: switch phase to FOCUS, reset focusRemainingSeconds to config full duration
     * Output: resumeTimer() starts a new focus countdown (cycles counter unchanged here)
     */
    private fun startNextFocusCycle() {
        val config = _uiState.value.config ?: return
        val totalSec = config.focusMinutes * 60
        _uiState.update {
            it.copy(
                phase = FocusPhase.FOCUS,
                focusRemainingSeconds = totalSec,
            )
        }
        resumeTimer()
    }

    /**
     * [ViewModel] [Effect]
     * Input: none (called when focus timer hits 0)
     * Process: increment cycles; NOTIFY → cycleCompletedEvent (sound);
     *          repeat=true → phase=BREAK + startBreakTimer();
     *          repeat=false → timerFinishedEvent for EndSessionDialog
     * Output: cycles+1; either break countdown or Channel event to UI
     */
    private suspend fun onFocusTimerFinished() {
        timerJob?.cancel()
        notificationHelper.cancel()
        val config = _uiState.value.config
        _uiState.update { it.copy(isRunning = false, cycles = it.cycles + 1) }

        if (config?.alertMode == AlertMode.NOTIFY) {
            _cycleCompletedEvent.send(Unit)
        }

        if (config?.repeat == true) {
            val breakSec = config.breakMinutes * 60
            _uiState.update {
                it.copy(phase = FocusPhase.BREAK, breakRemainingSeconds = breakSec)
            }
            startBreakTimer()
        } else {
            _timerFinishedEvent.send(Unit)
        }
    }

    /**
     * [ViewModel]
     * Input: none (breakRemainingSeconds already set by onFocusTimerFinished)
     * Process: bump timerGeneration → 1s coroutine decrements breakRemainingSeconds;
     *          showBreak notification every 5s
     * Output: on zero → onBreakFinished(); interrupted early by skipBreak()
     */
    private fun startBreakTimer() {
        _uiState.update {
            it.copy(isRunning = true, isPaused = false, timerGeneration = it.timerGeneration + 1)
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.breakRemainingSeconds > 0) {
                delay(1000)
                val newBreak = _uiState.value.breakRemainingSeconds - 1
                _uiState.update { it.copy(breakRemainingSeconds = newBreak) }
                if (newBreak % 5 == 0) {
                    notificationHelper.showBreak(_uiState.value.todo?.title, newBreak)
                }
            }
            if (_uiState.value.breakRemainingSeconds == 0 && _uiState.value.isRunning) {
                onBreakFinished()
            }
        }
    }

    /**
     * [ViewModel]
     * Input: none (break timer reached 0 naturally)
     * Process: cancel job + dismiss notification → auto-advance, no user action needed
     * Output: startNextFocusCycle() — Pomodoro loops back to FOCUS
     */
    private suspend fun onBreakFinished() {
        timerJob?.cancel()
        notificationHelper.cancel()
        _uiState.update { it.copy(isRunning = false) }
        startNextFocusCycle()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        notificationHelper.cancel()
    }
}
