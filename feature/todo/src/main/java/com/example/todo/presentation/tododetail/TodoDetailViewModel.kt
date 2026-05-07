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
