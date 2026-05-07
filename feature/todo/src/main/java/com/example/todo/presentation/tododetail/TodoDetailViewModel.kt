package com.example.todo.presentation.tododetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.TodoStatus
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
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
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
        }
    }

    private fun observeTodo() {
        viewModelScope.launch {
            todoRepository.observeAllTodo().map { list -> list.firstOrNull { it.id == todoId } }
                .collectLatest { todo ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            todo = todo
                        )
                    }
                }
        }
    }

    fun advanceStatus() {
        val current = _uiState.value.todo ?: return
        val nextStatus = current.status.nextInDetailWorkflow()
        val updated = current.copy(
            status = nextStatus,
            updatedAt = Clock.System.now(),
            completedAt = when (nextStatus) {
                TodoStatus.DONE -> Clock.System.now()
                else -> null
            },
        )
        viewModelScope.launch {
            val result = todoRepository.updateTodo(updated)
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()) }
            }
        }
    }

    fun deletedTodo() {
        viewModelScope.launch {
            val result = todoRepository.deleteTodo(todoId)
            if (result.isSuccess) {
                _deleted.send(Unit)
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()) }
            }
        }
    }

    private fun TodoStatus.nextInDetailWorkflow(): TodoStatus = when (this) {
        TodoStatus.TODO -> TodoStatus.IN_PROGRESS
        TodoStatus.IN_PROGRESS -> TodoStatus.DONE
        TodoStatus.DONE -> TodoStatus.TODO
        TodoStatus.PAUSED -> TodoStatus.IN_PROGRESS
    }
}