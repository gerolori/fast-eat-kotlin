package com.example.mangiaebasta.features.profile.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mangiaebasta.AppViewModel
import java.time.Year

@Composable
fun ProfileForm(navController: NavController, appViewModel: AppViewModel) {
    val user = appViewModel.userInfo.collectAsState().value
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var cardFullName by remember { mutableStateOf(user?.cardFullName ?: "") }
    var cardNumber by remember { mutableStateOf(user?.cardNumber ?: "") }
    var cardExpireMonth by remember { mutableStateOf(user?.cardExpireMonth?.toString() ?: "") }
    var cardExpireYear by remember { mutableStateOf(user?.cardExpireYear?.toString() ?: "") }
    var cardCVV by remember { mutableStateOf(user?.cardCVV ?: "") }
    var before by remember { mutableStateOf("profile") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var cardFullNameError by remember { mutableStateOf<String?>(null) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardExpireMonthError by remember { mutableStateOf<String?>(null) }
    var cardExpireYearError by remember { mutableStateOf<String?>(null) }
    var cardCVVError by remember { mutableStateOf<String?>(null) }

    var showExitDialog by remember { mutableStateOf(false) }
    var hasErrors by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (appViewModel.screen.value == "confirm_order") {
            before = "confirm_order"
        }
        appViewModel.setScreen("profile_form")
        if (user == null) {
            appViewModel.loadUserInfo()
        }
    }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(
        firstNameError,
        lastNameError,
        cardFullNameError,
        cardNumberError,
        cardExpireMonthError,
        cardExpireYearError,
        cardCVVError,
    ) {
        hasErrors = firstNameError != null ||
                lastNameError != null ||
                cardFullNameError != null ||
                cardNumberError != null ||
                cardExpireMonthError != null ||
                cardExpireYearError != null ||
                cardCVVError != null
    }

    fun onSaveClicked() {
        if (hasErrors) {
            Toast.makeText(context, "Fix the errors before saving", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUser = user?.copy(
            firstName = firstName,
            lastName = lastName,
            cardFullName = cardFullName,
            cardNumber = cardNumber,
            cardExpireMonth = cardExpireMonth.toInt(),
            cardExpireYear = cardExpireYear.toInt(),
            cardCVV = cardCVV
        )

        // Perform save operation and navigate to the appropriate screen
        appViewModel.updateUserInfo(updatedUser)
        navController.navigate(before)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Update Profile",
                    style = MaterialTheme.typography.titleLarge,
                )
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    UserField(
                        label = "First Name",
                        value = firstName,
                        error = firstNameError,
                        onValueChange = {
                            firstName = it
                            firstNameError = when {
                                it.isBlank() -> "First name cannot be empty"
                                it.length > 15 -> "First name too long"
                                else -> null
                            }
                        },
                        keyboardType = KeyboardType.Text
                    )
                }

                item {
                    UserField(
                        label = "Last Name",
                        value = lastName,
                        error = lastNameError,
                        onValueChange = {
                            lastName = it
                            lastNameError = when {
                                it.isBlank() -> "Last name cannot be empty"
                                it.length > 15 -> "Last name too long"
                                else -> null
                            }
                        },
                        keyboardType = KeyboardType.Text
                    )
                }

                item {
                    UserField(
                        label = "Card Full Name",
                        value = cardFullName,
                        error = cardFullNameError,
                        onValueChange = {
                            cardFullName = it
                            cardFullNameError = when {
                                it.isBlank() -> "Card full name cannot be empty"
                                it.length > 255 -> "Card full name too long"
                                !it.all { char -> char.isLetter() || char == ' ' } -> "Card full name must contain only letters and spaces"
                                else -> null
                            }
                        },
                        keyboardType = KeyboardType.Text
                    )
                }

                item {
                    UserField(
                        label = "Card Number",
                        value = cardNumber,
                        error = cardNumberError,
                        onValueChange = {
                            cardNumber = it
                            cardNumberError = when {
                                it.length != 16 -> "Card number must be 16 digits"
                                !it.all { char -> char.isDigit() } -> "Card number must contain only digits"
                                else -> null
                            }
                        },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {
                    UserField(
                        label = "Card Expire Month",
                        value = cardExpireMonth,
                        error = cardExpireMonthError,
                        onValueChange = {
                            cardExpireMonth = it
                            cardExpireMonthError =
                                if (cardExpireMonth.toIntOrNull() !in 1..12) "Invalid month" else null
                        },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {
                    UserField(
                        label = "Card Expire Year",
                        value = cardExpireYear,
                        error = cardExpireYearError,
                        onValueChange = {
                            cardExpireYear = it
                            cardExpireYearError =
                                if ((cardExpireYear.toIntOrNull()
                                        ?: 0) < Year.now().value
                                ) "Invalid year" else null
                        },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {
                    UserField(
                        label = "Card CVV", value = cardCVV, error = cardCVVError, onValueChange = {
                            cardCVV = it
                            cardCVVError = if (it.length != 3) "CVV must be 3 digits" else null
                        }, keyboardType = KeyboardType.Number
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = { onSaveClicked() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Done, contentDescription = "Save")
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes. Do you want to save or discard them?") },
            confirmButton = {
                Text(
                    text = "Save",
                    modifier = Modifier
                        .clickable {
                            showExitDialog = false
                            onSaveClicked()
                        }
                        .padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    text = "Discard",
                    modifier = Modifier
                        .clickable {
                            showExitDialog = false
                            navController.navigate(before)
                        }
                        .padding(8.dp)
                )
            }
        )
    }
}

@Composable
fun UserField(
    label: String,
    value: String,
    error: String? = null,
    onValueChange: (String) -> Unit = {},
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = { error?.let { Text(it) } },
    )
}