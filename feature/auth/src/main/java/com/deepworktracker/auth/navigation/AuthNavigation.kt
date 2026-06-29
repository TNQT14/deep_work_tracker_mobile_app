package com.deepworktracker.auth.navigation

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.deepworktracker.auth.ui.LoginRoute
import com.deepworktracker.auth.ui.RegisterRoute
import com.deepworktracker.auth.ui.forgot_password.ForgotPasswordRoute
import com.deepworktracker.auth.ui.reset_password.ForgotPasswordResetRoute

const val LOGIN_ROUTE = "auth/login"
const val REGISTER_ROUTE = "auth/register"
const val FORGOT_VERIFY_ROUTE = "auth/forgot-password/verify"
const val FORGOT_RESET_ROUTE = "auth/forgot-password/reset/{email}"


fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onAuthenticated: () -> Unit,
    sessionExpiredMessage: String? = null,
) {
    composable(LOGIN_ROUTE) {
        LoginRoute(
            onLoginSuccess = onAuthenticated,
            onNavigateToRegister = { navController.navigate(REGISTER_ROUTE) },
            onNavigateToForgotPassword = {
                navController.navigate(FORGOT_VERIFY_ROUTE)
            },
            sessionExpiredMessage = sessionExpiredMessage,
        )
    }
    composable(REGISTER_ROUTE) {
        RegisterRoute(
            onRegisterSuccess = onAuthenticated,
            onNavigateBackToLogin = { navController.popBackStack() },
        )
    }
    composable(FORGOT_VERIFY_ROUTE) {
        ForgotPasswordRoute(
            onEmailVerified = { email ->
                val encoded = Uri.encode(email)
                navController.navigate("auth/forgot-password/reset/$encoded")
            },
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable(
        FORGOT_RESET_ROUTE,
        arguments = listOf(
            navArgument("email") { type = NavType.StringType }
        )) { backStackEntry ->
        val rawEmail = backStackEntry.arguments?.getString("email").orEmpty()
        val email = Uri.decode(rawEmail)
        if(email.isBlank()){
            navController.popBackStack()
            return@composable
        }
        ForgotPasswordResetRoute(
            email = email,
            onResetSuccess = {
                navController.popBackStack(LOGIN_ROUTE, inclusive = false)
            },
            onNavigateBack = { navController.popBackStack() },
        )

    }
}
