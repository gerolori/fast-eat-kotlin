package com.example.mangiaebasta.old.menu.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuResponse(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
)

@Serializable
data class MenuDetailedResponse(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
    val longDescription: String,
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double,
)
