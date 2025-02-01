package com.example.mangiaebasta.old.menu.data.local

import com.example.mangiaebasta.old.menu.domain.model.Location

data class Order(
    val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    val deliveryTimestamp: String,
    val expectedDeliveryTimestamp: String,
    val currentPosition: Location,
)
