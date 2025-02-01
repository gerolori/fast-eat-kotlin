package com.example.mangiaebasta.old.menu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuNearRequest(
    val lat: Double,
    val lng: Double,
)
