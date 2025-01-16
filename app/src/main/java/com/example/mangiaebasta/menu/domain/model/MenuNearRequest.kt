package com.example.mangiaebasta.menu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuNearRequest(
    val lat: Double,
    val lng: Double,
)
