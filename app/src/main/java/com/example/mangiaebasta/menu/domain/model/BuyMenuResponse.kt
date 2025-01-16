package com.example.mangiaebasta.menu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BuyMenuResponse(
    val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    val deliveryTimestamp: String? = null,
    val expectedDeliveryTimestamp: String? = null,
    val currentPosition: Location,
)
