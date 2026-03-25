package com.deepworktracker.data.repository

import com.deepworktracker.data.database.dao.TodoDao
import com.deepworktracker.data.mapper.TodoMapper
import com.deepworktracker.domain.model.Todo
import com.deepworktracker.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val mapper: TodoMapper
) : TodoRepository {
    override fun observeAllTodo(): Flow<List<Todo>> {
        return todoDao.observeAllTodos().map { list -> list.map(mapper::toDomain) }
    }

    override fun observeTodosByGoal(goal: String): Flow<List<Todo>> {
        return todoDao.observeTodosByGoal(goal).map { list -> list.map(mapper::toDomain) }
    }

    override suspend fun createTodo(todo: Todo): Result<Unit> {
        return try {
            todoDao.insertTodo(mapper.toEntity(todo))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTodo(todo: Todo): Result<Unit> {
        return try {
            todoDao.updateTodo(mapper.toEntity(todo))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return try {
            todoDao.deleteTodo(id)
            Result.success(Unit)
        } catch (
            e: Exception
        ) {
            Result.failure(e)

        }
    }

}