package ir.adicom.kamkam.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Transaction : Screen("transaction")

    data object AddTransaction : Screen("add_transaction")

    data object Detail : Screen("detail/{id}") {
        fun createRoute(id: Long) = "detail/$id"
    }

    data object EditTransaction : Screen("edit_transaction/{id}") {
        fun createRoute(id: Long) = "edit_transaction/$id"
    }

    data object Report : Screen("report")

    data object Sample : Screen("sample")

    data object DetailSample : Screen("detail_sample")

    data object Setting : Screen("setting")

    data object Splash : Screen("splash")

    data object Categories: Screen("categories")
}