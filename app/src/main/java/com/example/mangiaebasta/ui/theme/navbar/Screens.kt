package com.example.mangiaebasta.ui.theme.navbar

sealed class Screens(val route : String) {
    data object Menu : Screens("menu_route")
    data object Profile : Screens("profile_route")
}