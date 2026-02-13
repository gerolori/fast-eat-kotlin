package com.example.mangiaebasta.features.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen


@Composable
fun ProfilePage(navController: NavHostController, appViewModel: AppViewModel) {

    val userInfo = appViewModel.userInfo.collectAsState().value
    var showCardDetails by remember { mutableStateOf(false) }

    LaunchedEffect(userInfo) {
        appViewModel.setScreen("profile")
        if (userInfo == null) {
            appViewModel.loadUserInfo()
        }
    }

    if (userInfo != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome, ${userInfo.firstName ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                IconButton(onClick = { navController.navigate("profile_form") }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                    )
                }
            }

            Section(
                sectionName = "Account Information"
            ) {
                UserField(label = "First Name", value = userInfo.firstName ?: "Not Provided")
                UserField(label = "Last Name", value = userInfo.lastName ?: "Not Provided")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SectionWithToggle(
                sectionName = "Card Details",
                showToggle = showCardDetails,
                onToggleClick = { showCardDetails = !showCardDetails }
            ) {
                UserField(label = "Full Name", value = userInfo.cardFullName ?: "Not Provided")
                UserField(
                    label = "Number",
                    value = if (showCardDetails) userInfo.cardNumber
                        ?: "Not Provided" else "**** **** **** ****"
                )
                UserField(
                    label = "CVV",
                    value = if (showCardDetails) userInfo.cardCVV ?: "Not Provided" else "***"
                )
                UserField(
                    label = "Expiry",
                    value = if (showCardDetails) "${userInfo.cardExpireMonth ?: "Not Provided"}/${userInfo.cardExpireYear ?: "Not Provided"}" else "**/**"
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Section(
                sectionName = "Last Order"
            ) {
                UserField(label = "Status", value = userInfo.orderStatus ?: "No order yet")
                if (userInfo.orderStatus != null) {
                    UserField(label = "ID", value = userInfo.lastOid.toString())
                }
            }
        }
    } else {
        SplashLoadingScreen()
    }
}

@Composable
fun Section(
    sectionName: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = sectionName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        content()
    }
}

@Composable
fun SectionWithToggle(
    sectionName: String,
    showToggle: Boolean,
    onToggleClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            IconButton(onClick = onToggleClick) {
                Icon(
                    imageVector = if (showToggle) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showToggle) "Hide" else "Show"
                )
            }
        }
        content()
    }
}

@Composable
fun UserField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}