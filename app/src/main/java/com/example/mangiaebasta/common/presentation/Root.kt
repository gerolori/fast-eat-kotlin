package com.example.mangiaebasta.common.presentation


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.features.menu.presentation.ConfirmOrderPage
import com.example.mangiaebasta.features.menu.presentation.MenuDetailsPage
import com.example.mangiaebasta.features.menu.presentation.MenuPage
import com.example.mangiaebasta.features.order.presentation.OrderPage
import com.example.mangiaebasta.features.profile.presentation.ProfileForm
import com.example.mangiaebasta.features.profile.presentation.ProfilePage
import com.example.roomexample.R

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Root(appViewModel: AppViewModel) {

    val navController = rememberNavController()

    val screen = appViewModel.screen.value ?: "home"
    val tabScreen = remember { mutableStateOf(getTabScreenFromScreen(screen)) }


    Scaffold(bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBarItem(selected = currentRoute?.startsWith("menu") == true ||
                    currentRoute?.startsWith("confirm_order") == true ||
                    currentRoute?.startsWith("home") == true,
                onClick = { navController.navigate("home") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.menu_list),
                        contentDescription = "Menu",
                    )
                },
                label = { Text("Menu") })

            NavigationBarItem(selected = currentRoute == "order",
                onClick = { navController.navigate("order") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.shopping_bag),
                        contentDescription = "Order",
                    )
                },
                label = { Text("Order") })

            NavigationBarItem(selected = currentRoute?.startsWith("profile") == true,
                onClick = { navController.navigate("profile") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile",
                    )
                },
                label = { Text("Profile") })
        }
    }) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = tabScreen.value,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Menu Stack
            navigation(
                startDestination = (if (tabScreen.value == "home_stack") screen else "home"),
                route = "home_stack"
            ) {
                composable("home") { MenuPage(navController, appViewModel) }
                composable("menu") { MenuDetailsPage(navController, appViewModel) }
                composable("confirm_order") { ConfirmOrderPage(navController, appViewModel) }
            }

            // Order Page
            composable("order") { OrderPage(appViewModel) }

            // Profile Stack
            navigation(
                startDestination = (if (tabScreen.value == "profile_stack") screen else "profile"),
                route = "profile_stack"
            ) {
                composable("profile") { ProfilePage(navController, appViewModel) }
                composable("profile_form") { ProfileForm(navController, appViewModel) }
            }


        }
    }

}

fun getTabScreenFromScreen(screen: String): String {

    return when (screen) {
        "menu", "confirm_order", "home" -> "home_stack"
        "order" -> "order"
        "profile", "profile_form" -> "profile_stack"
        else -> "home_stack"
    }
}