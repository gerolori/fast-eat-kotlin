package com.example.mangiaebasta.menu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class Menu(
    @PrimaryKey val mid: Int,
    val name: String,
    val price: Double,
    val lat: Double,
    val lng: Double,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
    val longDescription: String?,
    val image: String,
)
