package ir.adicom.kamkam.ui.navigation

import android.net.http.SslCertificate.saveState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.adicom.kamkam.R
import ir.adicom.kamkam.ui.day.DayDetailRoute
import ir.adicom.kamkam.ui.edit.EditLogRoute
import ir.adicom.kamkam.ui.home.HomeRoute
import ir.adicom.kamkam.ui.plan.CreatePlanRoute
import ir.adicom.kamkam.ui.report.ReportRoute
import ir.adicom.kamkam.ui.splash.SplashScreen

private const val KEY_DELETED_LOG = "deleted_log_id"

private data class Tab(val route: String, val labelRes: Int, val icon: ImageVector)

private val tabs = listOf(
    Tab(Screen.Home.route, R.string.nav_home, Icons.Filled.Home),
    Tab(Screen.Report.route, R.string.nav_report, Icons.AutoMirrored.Filled.List),
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar() {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) },
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(inner).consumeWindowInsets(inner),
        ) {
            composable(Screen.Home.route) { entry ->
                val deleted by entry.savedStateHandle
                    .getStateFlow<Long?>(KEY_DELETED_LOG, null)
                    .collectAsStateWithLifecycle()

                HomeRoute(
                    onOpenEdit = {
                        navController.navigate(Screen.EditLog.createRoute(it))
                    },
                    onOpenCraving = {},
                    onOpenAddCustomTime = {
                        navController.navigate(Screen.EditLog.createRoute(0L))
                    },
                    deletedLogId = deleted,
                    onOpenSettings = {},
                    onOpenCreatePlan = {
                        navController.navigate(Screen.CreatePlan.route)
                    },
                )
            }

            composable(Screen.Report.route) {
                ReportRoute(onOpenDay = { navController.navigate(Screen.Day.createRoute(it)) })
            }
            composable(
                route = Screen.Day.route,
                arguments = listOf(navArgument("epochDay") { type = NavType.LongType }),
            ) { entry ->
                val deleted by entry.savedStateHandle
                    .getStateFlow<Long?>(KEY_DELETED_LOG, null)
                    .collectAsStateWithLifecycle()

                DayDetailRoute(
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Screen.EditLog.createRoute(it)) },
                    deletedLogId = deleted,
                    onDeletedLogHandled = { entry.savedStateHandle[KEY_DELETED_LOG] = null },
                )
            }

            composable(
                Screen.Splash.route
            ) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                Screen.CreatePlan.route
            ) {
                CreatePlanRoute(
                    onBack = {
                        navController.popBackStack()
                    },
                    onCreated = {
                        navController.popBackStack()
                    },
                )
            }

            composable(
                route = Screen.EditLog.route,
                arguments = listOf(navArgument("logId") { type = NavType.LongType }),
            ) {
                EditLogRoute(
                    onClose = {
                        navController.popBackStack()
                    },
                    onDeleted = { id ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            KEY_DELETED_LOG,
                            id
                        )
                        navController.popBackStack()
                    }
                )
            }
        }
    }


}