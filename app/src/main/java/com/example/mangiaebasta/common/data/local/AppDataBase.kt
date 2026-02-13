package com.example.mangiaebasta.common.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangiaebasta.common.model.ImageDB
import com.example.mangiaebasta.features.menu.data.ImagesDao

@Database(entities = [ImageDB::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun imagesDao(): ImagesDao
}

