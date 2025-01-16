package com.example.mangiaebasta.ui.theme.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Menu,
    val route: String = "",
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> =
        listOf(
            BottomNavigationItem(
                label = "Menu",
                icon = Icons.Filled.Menu,
                route = Screens.Menu.route,
            ),
            BottomNavigationItem(
                label = "Profile",
                icon = Icons.Filled.AccountCircle,
                route = Screens.Profile.route,
            ),
            BottomNavigationItem(
                label = "Orders",
                icon = Icons.Filled.Notifications,
                route = Screens.Orders.route,
            ),
        )
}
