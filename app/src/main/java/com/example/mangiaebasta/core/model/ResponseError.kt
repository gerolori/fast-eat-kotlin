package com.example.mangiaebasta.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
    val message: String,
)
