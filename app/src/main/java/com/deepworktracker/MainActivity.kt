package com.deepworktracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.deepworktracker.auth.navigation.LOGIN_ROUTE
import com.deepworktracker.auth.navigation.authGraph
import com.deepworktracker.dashboard.presentation.category.CategoryScreen
import com.deepworktracker.dashboard.presentation.category_detail.CategoryDetailScreen
import com.deepworktracker.dashboard.presentation.dashboard.DashboardScreen
import com.deepworktracker.dashboard.presentation.goal_detail.GoalDetailScreen
import com.deepworktracker.data.remote.auth.AuthSessionState
import com.deepworktracker.preferences.LanguageManager
import com.deepworktracker.preferences.ThemeManager
import com.deepworktracker.profile.navigation.BLOCKLIST_ROUTE
import com.deepworktracker.profile.navigation.SETTINGS_ROUTE
import com.deepworktracker.profile.presentation.blocklist_screen.BlocklistRoute
import com.deepworktracker.profile.presentation.profile_screen.ProfileRoute
import com.deepworktracker.profile.presentation.setting_screen.SettingRoute
import com.deepworktracker.session.presentation.SessionScreen
import com.deepworktracker.session.presentation.summary.SessionSummaryScreen
import com.deepworktracker.startup.AppSessionViewModel
import com.deepworktracker.startup.BootstrapErrorScreen
import com.deepworktracker.startup.SplashScreen
import com.deepworktracker.ui.DeepWorkAppRoot
import com.example.todo.navigation.todoGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var languageManager: LanguageManager

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted or not — FGS notification degrades gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()

        setContent {
            DeepWorkAppRoot(
                themeManager = themeManager,
                languageManager = languageManager,
            ) {
                val appSessionViewModel: AppSessionViewModel = hiltViewModel()
                val sessionState by appSessionViewModel.sessionState.collectAsStateWithLifecycle()

                when (val state = sessionState) {
                    AuthSessionState.Bootstrapping,
                    AuthSessionState.RestoringSession -> SplashScreen()

                    is AuthSessionState.BootstrapFailed -> BootstrapErrorScreen(
                        onRetry = appSessionViewModel::bootstrap,
                        onLogin = appSessionViewModel::continueToLogin,
                    )

                    else -> MainAppScaffold(
                        startDestination = if (state is AuthSessionState.Authenticated) {
                            "session"
                        } else {
                            LOGIN_ROUTE
                        },
                        showSessionExpiredMessage = state is AuthSessionState.SessionExpired,
                    )
                }
            }
        }
    }

    /** Ask for POST_NOTIFICATIONS (API 33+) so the focus-session foreground notification can show. */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun MainAppScaffold(
    startDestination: String,
    showSessionExpiredMessage: Boolean = false,
) {
    val sessionExpiredMessage = if (showSessionExpiredMessage) {
        stringResource(R.string.session_expired_message)
    } else {
        null
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val hideBottomBar =
                currentRoute?.startsWith("auth") == true ||
                        currentRoute == "goal" ||
                        currentRoute == SETTINGS_ROUTE ||
                        currentRoute == BLOCKLIST_ROUTE

            if (!hideBottomBar) {
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
                    },
                )
            }
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                authGraph(
                    navController = navController,
                    onAuthenticated = {
                        navController.navigate("session") {
                            popUpTo(LOGIN_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    sessionExpiredMessage = sessionExpiredMessage,
                )

                composable("session") {
                    SessionScreen(
                        onNavigateToDashboard = {
                            navController.navigate("dashboard")
                        },
                        onNavigateToSummary = { sessionId ->
                            navController.navigate("session/summary/${Uri.encode(sessionId)}")
                        },
                    )
                }

                composable("session/summary/{sessionId}") {
                    SessionSummaryScreen(
                        onBack = { navController.popBackStack() },
                    )
                }

                todoGraph(navController)

                composable("dashboard") {
                    DashboardScreen(
                        onNavigateToSession = {
                            navController.navigate("session")
                        },
                        onNavigateToGoal = { goal ->
                            navController.navigate("goal/${Uri.encode(goal)}")
                        },
                    )
                }

                composable("category") {
                    CategoryScreen(
                        onNavigateToCategoryDetail = { goal ->
                            navController.navigate("category/${Uri.encode(goal)}")
                        },
                    )
                }
                composable("category/{category}") { backStackEntry ->
                    val encoded = backStackEntry.arguments?.getString("category") ?: ""
                    val goal = Uri.decode(encoded)
                    CategoryDetailScreen(
                        goal = goal,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("goal/{goal}") { backStackEntry ->
                    val encoded = backStackEntry.arguments?.getString("goal") ?: ""
                    val goal = Uri.decode(encoded)
                    GoalDetailScreen(
                        goal = goal,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("profile") {
                    ProfileRoute(
                        onLogoutSuccess = {
                            navController.navigate(LOGIN_ROUTE) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToSettings = { navController.navigate(SETTINGS_ROUTE) },
                    )
                }

                composable(SETTINGS_ROUTE) {
                    SettingRoute(
                        onBack = { navController.popBackStack() },
                        onNavigateToBlocklist = { navController.navigate(BLOCKLIST_ROUTE) },
                    )
                }

                composable(BLOCKLIST_ROUTE) {
                    BlocklistRoute(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomNavItem.Session,
        BottomNavItem.Todo,
        BottomNavItem.Dashboard,
        BottomNavItem.Category,
        BottomNavItem.Profile,
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.titleRes),
                    )
                },
                label = {
                    Text(stringResource(item.titleRes))
                },
            )
        }
    }
}

sealed class BottomNavItem(
    @StringRes val titleRes: Int,
    val route: String,
    val icon: ImageVector,
) {
    object Session : BottomNavItem(R.string.nav_session, "session", Icons.Default.Home)
    object Todo : BottomNavItem(R.string.nav_todo, "todo", Icons.Default.Checklist)
    object Category : BottomNavItem(R.string.nav_goal, "category", Icons.Default.Star)
    object Dashboard : BottomNavItem(R.string.nav_dashboard, "dashboard", Icons.Default.Search)
    object Profile : BottomNavItem(R.string.nav_profile, "profile", Icons.Default.Person)
}
