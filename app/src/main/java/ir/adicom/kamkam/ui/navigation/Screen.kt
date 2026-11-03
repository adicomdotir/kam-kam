package ir.adicom.kamkam.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Splash : Screen("splash")

    data object CreatePlan: Screen("create_plan")
}