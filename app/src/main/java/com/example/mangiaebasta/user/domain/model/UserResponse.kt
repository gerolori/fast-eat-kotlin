package com.example.mangiaebasta.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val sid: String,
    val uid: Int,
)
