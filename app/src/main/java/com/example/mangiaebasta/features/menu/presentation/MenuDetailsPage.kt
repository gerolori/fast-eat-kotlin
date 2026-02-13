package com.example.mangiaebasta.features.menu.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen
import com.example.mangiaebasta.common.model.MenuResponseFromGetandImage

@Composable
fun MenuDetailsPage(navController: NavHostController, appViewModel: AppViewModel) {
    var menu by remember { mutableStateOf(null as MenuResponseFromGetandImage?) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        appViewModel.setScreen("menu")
        appViewModel.loadUserInfo()
        menu = appViewModel.getMenu()
    }

    if (menu != null) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(onClick = {
                        navController.navigate("home")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    }
                }

                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = menu!!.image.asImageBitmap(),
                            contentDescription = "Menu image",
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = menu!!.menuResponseFromGet.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        menu!!.menuResponseFromGet.longDescription.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(horizontal = 30.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            Row(
                modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = "€${menu!!.menuResponseFromGet.price}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "delivery in ${menu!!.menuResponseFromGet.deliveryTime} minutes",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = {
                        appViewModel.isValidUserInfo { isValid ->
                            if (!isValid) {
                                showDialog = true
                            } else {
                                navController.navigate("confirm_order")
                            }
                        }
                    },
                ) {
                    Text("Buy")
                }
            }

        }
    } else {
        SplashLoadingScreen()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Error") },
            text = { Text("Please complete your profile information before ordering.") },
            confirmButton = {
                Text(
                    text = "Go to profile",
                    modifier = Modifier
                        .clickable {
                            showDialog = false
                            navController.navigate("profile_form")
                        }
                )
            }
        )
    }
}