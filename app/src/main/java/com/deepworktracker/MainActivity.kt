package com.deepworktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.net.Uri
import com.deepworktracker.dashboard.presentation.DashboardScreen
import com.deepworktracker.dashboard.presentation.GoalDetailScreen
import com.deepworktracker.session.presentation.SessionScreen
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeepWorkTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "session"
                    ) {
                        composable("session") {
                            SessionScreen(
                                onNavigateToDashboard = { navController.navigate("dashboard") }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToSession = { navController.navigate("session") },
                                onNavigateToGoal = { goal ->
                                    navController.navigate("goal/${Uri.encode(goal)}")
                                }
                            )
                        }

                        composable("goal/{goal}") { backStackEntry ->
                            val encoded = backStackEntry.arguments?.getString("goal") ?: ""
                            val goal = Uri.decode(encoded)
                            GoalDetailScreen(goal = goal)
                        }
                    }
                }
            }
        }
    }
}
