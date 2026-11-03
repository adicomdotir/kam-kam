package ir.adicom.kamkam.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.adicom.kamkam.ui.edit.EditLogRoute
import ir.adicom.kamkam.ui.home.HomeRoute
import ir.adicom.kamkam.ui.plan.CreatePlanRoute
import ir.adicom.kamkam.ui.splash.SplashScreen

private const val KEY_DELETED_LOG = "deleted_log_id"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
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
                    navController.previousBackStackEntry?.savedStateHandle?.set(KEY_DELETED_LOG, id)
                    navController.popBackStack()
                }
            )
        }
    }


}