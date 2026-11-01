package ir.adicom.kamkam.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.adicom.kamkam.ui.home.HomeActions
import ir.adicom.kamkam.ui.home.HomeScreen
import ir.adicom.kamkam.ui.home.HomeUiState
import ir.adicom.kamkam.ui.home.LogItemUi
import ir.adicom.kamkam.ui.home.PlanUi
import ir.adicom.kamkam.ui.splash.SplashScreen

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
             val sampleState = HomeUiState(
                dateText = "یکشنبه ۱۰ آبان ۱۴۰۵",
                todayCount = 3,
                plan = PlanUi(stageLabel = "مرحله‌ی ۲", target = 5),
                sinceLastText = "۱ ساعت و ۴۰ دقیقه",
                logs = listOf(
                    LogItemUi(3, "۱۴:۲۰", "بعد غذا"),
                    LogItemUi(2, "۱۰:۵۰", "قهوه"),
                    LogItemUi(1, "۰۷:۴۰", "بیدار شدن"),
                ),
            )
            HomeScreen(
                state = sampleState,
                actions = HomeActions()
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