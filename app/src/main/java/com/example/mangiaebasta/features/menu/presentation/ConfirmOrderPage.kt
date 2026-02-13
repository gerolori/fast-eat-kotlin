package com.example.mangiaebasta.features.menu.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ConfirmOrderPage(navController: NavHostController, appViewModel: AppViewModel) {
    var menuName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val exceptionMessage = remember { mutableStateOf("You already have an active order") }

    LaunchedEffect(Unit) {
        appViewModel.setScreen("confirm_order")
        menuName = appViewModel.getMenuName()
        address = appViewModel.getAddress()
        name = appViewModel.getUserFirstAndLastName()
    }

    if (menuName.isNotEmpty() && address.isNotEmpty() && name.isNotEmpty()) {
        ConfirmOrderContent(
            menuName = menuName,
            address = address,
            name = name,
            onConfirmClick = {
                appViewModel.viewModelScope.launch {
                    onClickOnButton(appViewModel, navController, showDialog, exceptionMessage)
                }
            },
            showDialog = showDialog.value,
            exceptionMessage = exceptionMessage.value,
            onDialogDismiss = { showDialog.value = false }
        )
    } else {
        SplashLoadingScreen()
    }
}

@Composable
fun ConfirmOrderContent(
    menuName: String,
    address: String,
    name: String,
    onConfirmClick: () -> Unit,
    showDialog: Boolean,
    exceptionMessage: String,
    onDialogDismiss: () -> Unit
) {
    Column(
        modifier = Modifier

            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Confirm Your Order",
            style = MaterialTheme.typography.headlineSmall,
        )
        OrderDetails(menuName = menuName, address = address, name = name)
        ConfirmButton(onClick = onConfirmClick)
        if (showDialog) {
            ErrorDialog(exceptionMessage = exceptionMessage, onDismiss = onDialogDismiss)
        }
    }
}

@Composable
fun OrderDetails(menuName: String, address: String, name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailText(label = "Name", value = name)
        DetailText(label = "Address", value = address)
        DetailText(label = "Menu", value = menuName)
    }
}

@Composable
fun DetailText(label: String, value: String) {
    Column {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ConfirmButton(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp)
    ) {
        Text("Confirm Order")
    }
}

@Composable
fun ErrorDialog(exceptionMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(exceptionMessage) },
        confirmButton = {
            Text(
                text = "OK",
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }
    )
}

suspend fun onClickOnButton(
    appViewModel: AppViewModel,
    navController: NavHostController,
    showDialog: MutableState<Boolean>,
    exceptionMessage: MutableState<String>
) {
    try {
        appViewModel.userCanOrder { canOrder ->
            if (!canOrder) {
                appViewModel.createOrder({
                    navController.navigate("order")
                }) { string: String? ->
                    showDialog.value = true
                    if (string != null) {
                        exceptionMessage.value = string
                    }
                }
            } else {
                showDialog.value = true
            }
        }
    } catch (e: Exception) {
        Log.e("ConfirmOrderPage", "Error in onClickOnButton: ${e.message}")
    }
}