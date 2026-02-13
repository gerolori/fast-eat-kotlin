package com.example.mangiaebasta.features.menu.presentation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen

@Composable
fun MenuPage(navController: NavHostController, appViewModel: AppViewModel) {
    val menuList = appViewModel.menuList.collectAsState().value

    LaunchedEffect(Unit) {
        appViewModel.setScreen("home")
        appViewModel.getNearMenus()
        appViewModel.getAddress()
    }

    if (menuList != null) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Choose your menu",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp) // Space between items
            ) {
                items(menuList) {
                    MenuListItem(
                        menu = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.d("HomePage", "Menu clicked  -  MID: ${it.nearMenu.mid}")
                                appViewModel.setLastMenuMid(it.nearMenu.mid)
                                navController.navigate("menu")
                            }
                    )
                }
            }
        }
    } else {
        SplashLoadingScreen()
    }
}