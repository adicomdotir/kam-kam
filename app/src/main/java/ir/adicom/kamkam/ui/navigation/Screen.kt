package ir.adicom.kamkam.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Splash : Screen("splash")

    data object CreatePlan: Screen("create_plan")

    data object EditLog : Screen("edit_log/{logId}") {
        fun createRoute(id: Long) = "edit_log/$id"
    }

    data object Report : Screen("report")

    data object Day : Screen("day/{epochDay}") {
        fun createRoute(epochDay: Long) = "day/$epochDay"
    }
}