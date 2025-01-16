package com.example.mangiaebasta.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mangiaebasta.ui.theme.MangiaEBastaTheme
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.domain.model.UpdateUserRequest
import com.example.mangiaebasta.user.presentation.UserViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileScreen(
    navController: NavController, // NOSONAR
    userId: Int,
    userViewModel: UserViewModel,
) {
    val userState = userViewModel.userData.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var updatedUser by remember { mutableStateOf<User?>(null) }
    var hasErrors by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userViewModel.getUser(userId)
    }

    MangiaEBastaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
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
                            "Profile Screen",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (isEditMode) "Save" else "Edit",
                            )
                        }
                    }
                    LazyColumn {
                        item {
                            userState.value?.let { user ->
                                if (isEditMode) {
                                    updatedUser = EditableUserFields(user, onValidationChange = { hasErrors = it })
                                } else {
                                    DisplayUserFields(user)
                                }
                            }
                        }
                    }
                }

                if (isEditMode) {
                    FloatingActionButton(
                        onClick = {
                            if (hasErrors) {
                                Toast.makeText(context, "Fix the errors before saving", Toast.LENGTH_SHORT).show()
                                return@FloatingActionButton
                            } else {
                                updatedUser?.let { user ->
                                    userViewModel.updateUser(
                                        userId,
                                        UpdateUserRequest(
                                            firstName = user.firstName!!,
                                            lastName = user.lastName!!,
                                            cardFullName = user.cardFullName!!,
                                            cardNumber = user.cardNumber!!,
                                            cardExpireMonth = user.cardExpireMonth!!,
                                            cardExpireYear = user.cardExpireYear!!,
                                            cardCVV = user.cardCVV!!,
                                        ),
                                    )
                                    Toast
                                        .makeText(context, "User updated", Toast.LENGTH_SHORT)
                                        .show()
                                    isEditMode = false
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun DisplayUserFields(user: User) {
    Column {
        UserField("First Name", user.firstName.toString(), false)
        UserField("Last Name", user.lastName.toString(), false)
        UserField("Card Full Name", user.cardFullName.toString(), false)
        UserField("Card Number", user.cardNumber.toString(), false)
        UserField("Card Expire Month", user.cardExpireMonth.toString(), false)
        UserField("Card Expire Year", user.cardExpireYear.toString(), false)
        UserField("Card CVV", user.cardCVV.toString(), false)
        UserField("Last Order ID", user.lastOid.toString(), false)
        UserField("Order Status", user.orderStatus.toString(), false)
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun EditableUserFields(
    user: User,
    onValidationChange: (Boolean) -> Unit,
): User {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var cardFullName by remember { mutableStateOf(user.cardFullName) }
    var cardNumber by remember { mutableStateOf(user.cardNumber) }
    var cardExpireMonth by remember { mutableStateOf(user.cardExpireMonth) }
    var cardExpireYear by remember { mutableStateOf(user.cardExpireYear) }
    var cardCVV by remember { mutableStateOf(user.cardCVV) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var cardFullNameError by remember { mutableStateOf<String?>(null) }
    var cardNumberError by remember { mutableStateOf<String?>(null) }
    var cardExpireMonthError by remember { mutableStateOf<String?>(null) }
    var cardExpireYearError by remember { mutableStateOf<String?>(null) }
    var cardCVVError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(
        firstNameError,
        lastNameError,
        cardFullNameError,
        cardNumberError,
        cardExpireMonthError,
        cardExpireYearError,
        cardCVVError,
    ) {
        onValidationChange(
            firstNameError != null ||
                lastNameError != null ||
                cardFullNameError != null ||
                cardNumberError != null ||
                cardExpireMonthError != null ||
                cardExpireYearError != null ||
                cardCVVError != null,
        )
    }

    Column {
        UserField("First Name", firstName.toString(), true, firstNameError) {
            firstName = it
            firstNameError =
                if (it.isBlank()) {
                    "First name cannot be empty"
                } else if (it.length > 15) {
                    "First name too long"
                } else {
                    null
                }
        }
        UserField("Last Name", lastName.toString(), true, lastNameError) {
            lastName = it
            lastNameError =
                if (it.isBlank()) {
                    "Last name cannot be empty"
                } else if (it.length > 15) {
                    "Last name too long"
                } else {
                    null
                }
        }
        UserField("Card Full Name", cardFullName.toString(), true, cardFullNameError) {
            cardFullName = it
            cardFullNameError =
                if (it.isBlank()) {
                    "Card full name cannot be empty"
                } else if (it.length > 255) {
                    "Card full name too long"
                } else if (!it.all { char -> char.isLetter() || char == ' ' }) {
                    "Card full name must contain only letters and spaces"
                } else {
                    null
                }
        }
        UserField("Card Number", cardNumber.toString(), true, cardNumberError) {
            cardNumber = it
            cardNumberError =
                if (it.length != 16) {
                    "Card number must be 16 digits"
                } else if (!it.all { char -> char.isDigit() }) {
                    "Card number must contain only digits"
                } else {
                    null
                }
        }
        UserField("Card Expire Month", cardExpireMonth.toString(), true, cardExpireMonthError) {
            cardExpireMonth = it.toIntOrNull() ?: 0
            cardExpireMonthError = if (cardExpireMonth !in 1..12) "Invalid month" else null
        }
        UserField("Card Expire Year", cardExpireYear.toString(), true, cardExpireYearError) {
            cardExpireYear = it.toIntOrNull() ?: 0
            cardExpireYearError = if (cardExpireYear!! < 2023) "Invalid year" else null
        }
        UserField("Card CVV", cardCVV.toString(), true, cardCVVError) {
            cardCVV = it
            cardCVVError = if (it.length != 3) "CVV must be 3 digits" else null
        }
    }

    return user.copy(
        firstName = firstName,
        lastName = lastName,
        cardFullName = cardFullName,
        cardNumber = cardNumber,
        cardExpireMonth = cardExpireMonth,
        cardExpireYear = cardExpireYear,
        cardCVV = cardCVV,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserField(
    label: String,
    value: String,
    isEditable: Boolean,
    error: String? = null,
    onValueChange: (String) -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
    ) {
        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { error?.let { Text(it) } },
            )
        } else {
            Text(
                text = "$label: $value",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
