package ir.adicom.kamkam.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.adicom.kamkam.ui.home.HomeActions
import ir.adicom.kamkam.ui.home.HomeRoute
import ir.adicom.kamkam.ui.home.HomeScreen
import ir.adicom.kamkam.ui.home.HomeUiState
import ir.adicom.kamkam.ui.home.LogItemUi
import ir.adicom.kamkam.ui.home.PlanUi
import ir.adicom.kamkam.ui.home.PostLogSheetUi
import ir.adicom.kamkam.ui.splash.SplashScreen

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
        composable(Screen.Home.route) {
            HomeRoute(
                onOpenEdit = {  },
                onOpenCraving = {},
                onOpenAddCustomTime = {  },
                onOpenSettings = {},
                onOpenCreatePlan = {  },
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
    }


}