package com.example.todo.presentation.todolist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.model.TodoStatus
import com.deepworktracker.domain.repository.SessionRepository
import com.deepworktracker.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository, private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoListUiState(isLoading = true))
    val uiState: StateFlow<TodoListUiState> = _uiState
    var _rawTodos: List<Todo> = emptyList()


    init {
        observeTodos()
        getAllSession()
    }

    private fun observeTodos() {
        viewModelScope.launch {
            todoRepository.observeAllTodo().collectLatest { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false, error = null, todos = list
                    )
                }
                _rawTodos = list
                applySort()
            }

        }
    }

    private fun getAllSession() {
        viewModelScope.launch {
            val allSession = sessionRepository.getAllSessions()

            _uiState.update {
                it.copy(session = allSession)
            }
        }
    }

    fun addTodo(goal: String, title: String, description: String) {
        val goal = goal.trim()
        val title = title.trim()

        if (goal.isEmpty() || title.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val now = Clock.System.now()
            val todo = Todo(
                id = UUID.randomUUID().toString(),
                goal = goal,
                title = title,
                description = description,
                status = TodoStatus.TODO,
                priority = 0,
                dueAt = null,
                completedAt = null,
                createdAt = now,
                updatedAt = now
            )

            val result = todoRepository.createTodo(todo)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()) }
            }

        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            val result = todoRepository.updateTodo(todo.copy(status = todo.status.toggle()))
            try {
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                    Log.d(TAG, "updateTodo: success")
                } else {
                    _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()) }
                    Log.d(TAG, "updateTodo: fail")
                }
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todo.id)
                _uiState.update {
                    it.copy(isLoading = false, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e)
                }
            }
        }
    }

    fun onSortTypeChange(type: TodoSortType) {
        _uiState.update { it.copy(sortType = type) }
        applySort()
    }

    fun onStatusFilterChange(status: TodoStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        applySort()
    }

    private fun applySort() {
        val filtered = _uiState.value.selectedStatus
            ?.let { status -> _rawTodos.filter { it.status == status } }
            ?: _rawTodos

        val sorted = when (_uiState.value.sortType) {
            TodoSortType.NAME -> filtered.sortedBy { it.title }
            TodoSortType.CREATED_AT -> filtered.sortedByDescending { it.createdAt }
            TodoSortType.GOAL -> filtered.sortedBy { it.goal }
        }

        _uiState.update { it.copy(todos = sorted) }
    }

    fun TodoStatus.toggle(): TodoStatus {
        return if (this == TodoStatus.DONE) TodoStatus.TODO else TodoStatus.DONE
    }

    companion object {
        private const val TAG = "TodoListViewModel"
    }
}