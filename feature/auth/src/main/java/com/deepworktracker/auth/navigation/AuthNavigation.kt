package com.deepworktracker.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.deepworktracker.auth.ui.LoginRoute
import com.deepworktracker.auth.ui.RegisterRoute

const val LOGIN_ROUTE = "auth/login"
const val REGISTER_ROUTE = "auth/register"

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onAuthenticated: () -> Unit,
) {
    composable(LOGIN_ROUTE) {
        LoginRoute(
            onLoginSuccess = onAuthenticated,
            onNavigateToRegister = { navController.navigate(REGISTER_ROUTE) },
        )
    }
    composable(REGISTER_ROUTE) {
        RegisterRoute(
            onRegisterSuccess = onAuthenticated,
            onNavigateBackToLogin = { navController.popBackStack() },
        )
    }
}
