package com.example.todo.presentation.tododetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val todoId: String = savedStateHandle.get<String>("todoId")?.trim().orEmpty()
    private val _uiState = MutableStateFlow(TodoDetailUiState(isLoading = true))
    val uiState: StateFlow<TodoDetailUiState> = _uiState.asStateFlow()
    private val _deleted = Channel<Unit>(Channel.BUFFERED)
    val deleted = _deleted.receiveAsFlow()

    init {
        if (todoId.isEmpty()) {
            _uiState.value = TodoDetailUiState(
                isLoading = false,
                error = IllegalArgumentException("Invalid todoId"),
                todo = null,
            )
        } else {
            observeTodo()
            observeFocusSessionCount()
            loadGoalOptions()
        }
    }

    private fun observeFocusSessionCount() {
        viewModelScope.launch {
            sessionRepository.getSessionsByTodoId(todoId)
                .map { sessions -> sessions.count { it.endTime != null } }
                .collect { count -> _uiState.update { it.copy(focusSessionCount = count) } }
        }
    }

    private fun loadGoalOptions() {
        viewModelScope.launch {
            runCatching { sessionRepository.getAllSessions() }
                .onSuccess { sessions ->
                    val goals = sessions.map { it.goal }.distinct().sorted()
                    _uiState.update { it.copy(goalOptions = goals) }
                }
        }
    }

    private fun observeTodo() {
        viewModelScope.launch {
            todoRepository.observeAllTodo().map { list -> list.firstOrNull { it.id == todoId } }
                .collectLatest { todo ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            todo = todo,
                        )
                    }
                }
        }
    }

    fun setStatus(target: TodoStatus) {
        val current = _uiState.value.todo ?: return
        if (current.status == target) return
        val now = Clock.System.now()
        val updated = current.copy(
            status = target,
            updatedAt = now,
            completedAt = if (target == TodoStatus.DONE) now else null,
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingStatus = true) }
            val result = todoRepository.updateTodo(updated)
            _uiState.update {
                it.copy(
                    isSavingStatus = false,
                    error = result.exceptionOrNull(),
                )
            }
        }
    }

    suspend fun updateTodo(
        goal: String,
        title: String,
        description: String,
        estimatedMinutes: Int? = null,
    ): Boolean {
        val current = _uiState.value.todo ?: return false
        val g = goal.trim()
        val t = title.trim()
        if (g.isEmpty() || t.isEmpty()) return false
        val now = Clock.System.now()
        val updated = current.copy(
            goal = g,
            title = t,
            description = description,
            estimatedMinutes = estimatedMinutes,
            updatedAt = now,
        )
        _uiState.update { it.copy(isSavingEdit = true, error = null) }
        val result = todoRepository.updateTodo(updated)
        _uiState.update {
            it.copy(
                isSavingEdit = false,
                error = result.exceptionOrNull(),
            )
        }
        return result.isSuccess
    }

    suspend fun updateDeadline(deadline: Instant): Boolean {
        val current = _uiState.value.todo ?: return false
        val updated = current.copy(
            dueAt = deadline,
            updatedAt = Clock.System.now(),
        )
        _uiState.update{it.copy(isSavingEdit = true, error = null)}
        val result = todoRepository.updateTodo(updated)
        _uiState.update {
            it.copy(
                isSavingEdit = false,
                error = result.exceptionOrNull(),
            )
        }
        return result.isSuccess
    }

    suspend fun clearDeadline(): Boolean {
        val current = _uiState.value.todo ?: return false
        val updated = current.copy(
            dueAt = null,
            updatedAt = Clock.System.now(),
        )
        _uiState.update { it.copy(isSavingEdit = true, error = null) }
        val result = todoRepository.updateTodo(updated)
        _uiState.update {
            it.copy(
                isSavingEdit = false,
                error = result.exceptionOrNull(),
            )
        }
        return result.isSuccess
    }

    fun deletedTodo() {
        if (todoId.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            val result = todoRepository.deleteTodo(todoId)
            _uiState.update { it.copy(isDeleting = false) }
            if (result.isSuccess) {
                _deleted.send(Unit)
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()) }
            }
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}
