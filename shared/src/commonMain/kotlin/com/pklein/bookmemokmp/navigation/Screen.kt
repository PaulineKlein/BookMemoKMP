package com.pklein.bookmemokmp.navigation

sealed class Screen(val route: String) {
    data object Collection : Screen("collection")
    data object AddItem    : Screen("add_item")
    data object EditItem   : Screen("edit_item")
    data object Settings   : Screen("settings")
}
