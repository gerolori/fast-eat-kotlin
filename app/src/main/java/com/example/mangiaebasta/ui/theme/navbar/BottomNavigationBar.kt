package com.example.mangiaebasta.ui.theme.navbar

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mangiaebasta.core.SharedPreferencesUtils
import com.example.mangiaebasta.core.SharedPreferencesUtils.getLastVisitedPage
import com.example.mangiaebasta.ui.theme.screens.MenuScreen
import com.example.mangiaebasta.ui.theme.screens.ProfileScreen
import com.example.mangiaebasta.user.presentation.UserViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    context: Context,
    userUid: Int,
    userViewModel: UserViewModel,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            SharedPreferencesUtils.saveLastVisitedPage(context, destination.route)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { _, navigationItem ->
                    NavigationBarItem(
                        selected = navigationItem.route == currentDestination?.route,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label,
                            )
                        },
                        onClick = {
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (getLastVisitedPage(context) == Screens.Menu.route) Screens.Menu.route else Screens.Profile.route,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            composable(Screens.Menu.route) {
                MenuScreen(navController)
            }
            composable(Screens.Profile.route) {
                ProfileScreen(navController, userUid, userViewModel)
            }
        }
    }
}
