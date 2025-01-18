package com.example.mangiaebasta.menu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BuyMenuRequest(
    val mid: Int,
    val deliveryLocation: Location,
)
