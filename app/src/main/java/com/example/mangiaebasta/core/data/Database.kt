package com.example.mangiaebasta.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.data.local.UserDao

@Database(entities = [User::class], version = 3)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao
}
