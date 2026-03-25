package com.deepworktracker.dashboard.presentation.category_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepworktracker.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryTasksViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryTasksUiState())
    val uiState: StateFlow<CategoryTasksUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun loadGoal(goal: String) {
        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null, todos = emptyList()) }

        observeJob = viewModelScope.launch {
            todoRepository.observeTodosByGoal(goal).collectLatest { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        todos = list
                    )
                }
            }
        }
    }
}
