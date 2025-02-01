package com.example.mangiaebasta.old.user.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val sid: String,
    val uid: Int,
)
