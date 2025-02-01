package com.example.mangiaebasta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.mangiaebasta.old.core.theme.MangiaEBastaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MangiaEBastaTheme {
                Root()
            }
        }
    }
}

@Composable
fun HomePage(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Home Page")
        Button(onClick = {
            navController.navigate("menu")
        }) {
            Text("Go to Menu")
        }
    }
}

@Composable
fun MenuPage(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Menu Page")
        Button(onClick = {
            navController.navigate("confirm_order")
        }) {
            Text("Go to Confirm Order")
        }
    }
}

@Composable
fun ConfirmOrderPage(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Confirm Order Page")
        Button(onClick = {
            navController.navigate("profile_form")
        }) {
            Text("Go to Profile")
        }
        Button(onClick = {
            navController.navigate("order")
        }) { Text("Go to Order") }
    }
}

@Composable
fun OrderPage() {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Order Page")
    }
}

@Composable
fun ProfilePage(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Profile Page")
        Button(onClick = {
            navController.navigate("profile_form")
        }) {
            Text("Go to Profile Form")
        }
    }
}

@Composable
fun ProfileForm(navController: NavHostController) {
    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Profile Form")
        Button(onClick = {
            navController.navigate("confirm_order")
        }) {
            Text("Go to Confirm Order")
        }
    }
}

@Composable
fun Root() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected =
                        currentRoute?.startsWith("menu") == true ||
                            currentRoute?.startsWith("confirm_order") == true ||
                            currentRoute?.startsWith("home") == true,
                    onClick = { navController.navigate("home_stack") },
                    icon = {
                        androidx.compose.material3.Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Home") },
                )

                NavigationBarItem(
                    selected = currentRoute == "order",
                    onClick = { navController.navigate("order") },
                    icon = {
                        androidx.compose.material3.Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Order",
                        )
                    },
                    label = { Text("Order") },
                )

                NavigationBarItem(
                    selected = currentRoute?.startsWith("profile") == true,
                    onClick = { navController.navigate("profile_stack") },
                    icon = {
                        androidx.compose.material3.Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                        )
                    },
                    label = { Text("Profile") },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_stack",
            modifier = Modifier.padding(innerPadding),
        ) {
            // Home Stack
            navigation(startDestination = "home", route = "home_stack") {
                composable("home") { HomePage(navController) }
                composable("menu") { MenuPage(navController) }
                composable("confirm_order") { ConfirmOrderPage(navController) }
            }

            // Order Page
            composable("order") { OrderPage() }

            // Profile Stack

            navigation(startDestination = "profile", route = "profile_stack") {
                composable("profile") { ProfilePage(navController) }
                composable("profile_form") { ProfileForm(navController) }
            }
        }
    }
}
