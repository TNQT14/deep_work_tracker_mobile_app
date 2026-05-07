package com.example.todo.navigation

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.deepworktracker.todo.presentation.TodoListScreen
import com.example.todo.presentation.tododetail.TodoDetailScreen

object TodoDestinations {
    const val LIST_ROUTE = "todo"
    const val DETAIL_ROUTE = "todo/{todoId}"
    fun detailRoute(todoId: String): String = "todo/${Uri.encode(todoId)}"
}

fun NavGraphBuilder.todoGraph(navController: NavHostController) {
    composable(TodoDestinations.LIST_ROUTE) {
        TodoListScreen(
            onNavigateToTodoDetail = { todoId ->
                navController.navigate(TodoDestinations.detailRoute(todoId))
            }
        )
    }

    composable(TodoDestinations.DETAIL_ROUTE) { backStackEntry ->
        val todoId = backStackEntry.arguments?.getString("todoId").orEmpty()
        TodoDetailScreen(
            todoId = Uri.decode(todoId),
            onBack = { navController.popBackStack() }
        )
    }
}
