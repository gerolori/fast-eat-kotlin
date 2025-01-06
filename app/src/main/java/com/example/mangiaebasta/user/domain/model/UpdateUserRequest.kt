package com.example.mangiaebasta.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val firstName: String,
    val lastName: String,
    val cardFullName: String,
    val cardNumber: String,
    val cardExpireMonth: Int,
    val cardExpireYear: Int,
    val cardCVV: String,
    val sid: String,
)
