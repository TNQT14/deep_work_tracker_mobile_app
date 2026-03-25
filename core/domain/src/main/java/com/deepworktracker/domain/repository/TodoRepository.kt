package com.deepworktracker.domain.repository

import com.deepworktracker.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeAllTodo(): Flow<List<Todo>>
    fun observeTodosByGoal(goal: String): Flow<List<Todo>>

    suspend fun createTodo(todo: Todo): Result<Unit>
    suspend fun updateTodo(todo: Todo): Result<Unit>
    suspend fun deleteTodo(id: String): Result<Unit>
}