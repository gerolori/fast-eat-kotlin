package com.example.mangiaebasta.core.navbar

sealed class Screens(
    val route: String,
) {
    data object Menu : Screens("menu_route")

    data object Profile : Screens("profile_route")

    data object Orders : Screens("orders_route")
}
