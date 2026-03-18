package com.deepworktracker

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import com.deepworktracker.dashboard.presentation.dashboard.DashboardScreen
import com.deepworktracker.dashboard.presentation.goal_detail.GoalDetailScreen
import com.deepworktracker.profile.presentation.profile_screen.ProfileScreen
import com.deepworktracker.session.presentation.SessionScreen
import com.deepworktracker.ui.theme.DeepWorkTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DeepWorkTrackerTheme {

                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != "goal") {
                            BottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo("session") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { padding ->

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        NavHost(
                            navController = navController,
                            startDestination = "session"
                        ) {

                            composable("session") {
                                SessionScreen(
                                    onNavigateToDashboard = {
                                        navController.navigate("dashboard")
                                    }
                                )
                            }

                            composable("dashboard") {
                                DashboardScreen(
                                    onNavigateToSession = {
                                        navController.navigate("session")
                                    },
                                    onNavigateToGoal = { goal ->
                                        navController.navigate("goal/${Uri.encode(goal)}")
                                    }
                                )
                            }

                            composable("category") {
                            }
                            composable("profile") {
                                ProfileScreen()
                            }

                            composable("goal/{goal}") { backStackEntry ->
                                val encoded =
                                    backStackEntry.arguments?.getString("goal") ?: ""
                                val goal = Uri.decode(encoded)
                                GoalDetailScreen(goal = goal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
){
  val items = listOf(
      BottomNavItem.Session,
      BottomNavItem.Dashboard,
      BottomNavItem.Category,
      BottomNavItem.Profile
  )

    NavigationBar {
        items.forEach {
            item -> NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }, label = {
                Text(item.title)
                }
            )
        }
    }

}


sealed class  BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
){
    object Session: BottomNavItem("Session", "session", Icons.Default.Home)
    object Category: BottomNavItem("Category", "category", Icons.Default.Star)
    object Dashboard: BottomNavItem("Dashboard", "dashboard", Icons.Default.Search)
    object Profile: BottomNavItem("Profile", "profile", Icons.Default.Person)
}
