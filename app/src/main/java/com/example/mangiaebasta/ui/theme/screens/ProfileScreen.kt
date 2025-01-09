package com.example.mangiaebasta.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mangiaebasta.ui.theme.MangiaEBastaTheme
import com.example.mangiaebasta.user.presentation.UserViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileScreen(
    navController: NavController, // NOSONAR
    userId: Int,
    userViewModel: UserViewModel,
) {
    val userState = userViewModel.userData.collectAsState()

    LaunchedEffect(userId) {
        userViewModel.getUser(userId)
    }

    MangiaEBastaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Profile Screen of ${userState.value?.firstName}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 20.dp),
                )
                LazyColumn {
                    item {
                        userState.value?.let { user ->
                            Text(
                                "First Name: ${user.firstName}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Last Name: ${user.lastName}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Card Full Name: ${user.cardFullName}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Card Number: ${user.cardNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Card Expire Month: ${user.cardExpireMonth}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Card Expire Year: ${user.cardExpireYear}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Card CVV: ${user.cardCVV}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Last Order ID: ${user.lastOid}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                "Order Status: ${user.orderStatus}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}
