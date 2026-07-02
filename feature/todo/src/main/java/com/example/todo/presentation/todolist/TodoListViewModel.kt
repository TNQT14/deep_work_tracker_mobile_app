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

/**
 * [ViewModel] [DI] [UDF]
 * Hilt-injected list screen logic. UI events flow up; TodoListUiState flows down via uiState.
 */
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository, private val sessionRepository: SessionRepository
) : ViewModel() {

    /**
     * [ViewModel]
     * Type: MutableStateFlow<TodoListUiState>
     * Sample: TodoListUiState(isLoading=true, todos=[], error=null)
     */
    private val _uiState = MutableStateFlow(TodoListUiState(isLoading = true))

    /**
     * [ViewModel]
     * Type: StateFlow<TodoListUiState>
     * Sample: TodoListUiState(isLoading=false, todos=[Todo(id="a1", title="Run")], error=null)
     */
    val uiState: StateFlow<TodoListUiState> = _uiState

    /**
     * [ViewModel]
     * Type: List<Todo>
     * Sample: [Todo(id="a1", goal="Health", title="Run 5km", status=TODO)]
     * Full unfiltered snapshot from Room; sort/filter derives uiState.todos from this.
     */
    var _rawTodos: List<Todo> = emptyList()

    /**
     * [ViewModel]
     * Input: (none)
     * Process: start observeTodos() Flow subscription; load focus sessions via getAllSession()
     * Output: background collectors active for ViewModel lifetime
     */
    init {
        observeTodos()
        getAllSession()
    }

    /**
     * [ViewModel] [Repository → ViewModel]
     * Input: (none)
     * Process: collectLatest on todoRepository.observeAllTodo()
     *          → sync _rawTodos → applySort()
     * Output: uiState.todos refreshed on every Room emission; isLoading=false, error=null
     */
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

    /**
     * [ViewModel] [Repository → ViewModel]
     * Input: (none)
     * Process: suspend sessionRepository.getAllSessions() → merge into uiState
     * Output: uiState.session = List<FocusSession> for goal dropdown in add form
     */
    private fun getAllSession() {
        viewModelScope.launch {
            val allSession = sessionRepository.getAllSessions()

            _uiState.update {
                it.copy(session = allSession)
            }
        }
    }

    /**
     * [ViewModel] [UDF]
     * Input: goal="Health", title="Run 5km", description="Morning jog"
     * Process: trim → validate non-empty → build Todo(UUID id) → todoRepository.createTodo()
     * Output: Unit; uiState.isLoading/error updated; list refreshes via observeTodos() Flow
     */
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

    /**
     * [ViewModel] [UDF]
     * Input: todo: Todo — e.g. Todo(id="a1", title="Run", status=TODO)
     * Process: toggle status (TODO↔DONE) → todoRepository.updateTodo()
     * Output: Unit; uiState.error on failure; list refresh via observeTodos()
     */
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

    /**
     * [ViewModel] [UDF]
     * Input: todo: Todo (uses todo.id) — e.g. Todo(id="a1", ...)
     * Process: suspend todoRepository.deleteTodo(id) in try/catch
     * Output: Unit; uiState.error set on catch
     */
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

    /**
     * [ViewModel]
     * Input: type: TodoSortType — e.g. TodoSortType.CREATED_AT
     * Process: update uiState.sortType → applySort() on _rawTodos
     * Output: Unit; uiState.todos reordered
     */
    fun onSortTypeChange(type: TodoSortType) {
        _uiState.update { it.copy(sortType = type) }
        applySort()
    }

    /**
     * [ViewModel]
     * Input: status: TodoStatus? — null (all) | TodoStatus.TODO | DONE | ...
     * Process: update uiState.selectedStatus → applySort()
     * Output: Unit; uiState.todos = filtered + sorted subset of _rawTodos
     */
    fun onStatusFilterChange(status: TodoStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        applySort()
    }

    /**
     * [ViewModel]
     * Input: reads _rawTodos, uiState.selectedStatus, uiState.sortType
     * Process: filter by selectedStatus (if set) → sort by sortType enum
     * Output: uiState.todos overwritten with filtered+sorted list
     */
    private fun applySort() {
        // Type: List<Todo> | Sample: todos matching selectedStatus, or full _rawTodos if null
        val filtered = _uiState.value.selectedStatus
            ?.let { status -> _rawTodos.filter { it.status == status } }
            ?: _rawTodos

        // Type: List<Todo> | Sample: filtered list sorted by title / createdAt / goal
        val sorted = when (_uiState.value.sortType) {
            TodoSortType.NAME -> filtered.sortedBy { it.title }
            TodoSortType.CREATED_AT -> filtered.sortedByDescending { it.createdAt }
            TodoSortType.GOAL -> filtered.sortedBy { it.goal }
        }

        _uiState.update { it.copy(todos = sorted) }
    }

    /**
     * [ViewModel]
     * Input: receiver TodoStatus — e.g. TodoStatus.TODO
     * Process: if DONE → TODO, else → DONE
     * Output: TodoStatus — e.g. TODO → DONE
     */
    fun TodoStatus.toggle(): TodoStatus {
        return if (this == TodoStatus.DONE) TodoStatus.TODO else TodoStatus.DONE
    }

    companion object {
        private const val TAG = "TodoListViewModel"
    }
}
