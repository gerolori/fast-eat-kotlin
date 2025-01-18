package com.example.mangiaebasta.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangiaebasta.menu.data.local.Menu
import com.example.mangiaebasta.menu.data.local.MenuDao
import com.example.mangiaebasta.user.data.local.User
import com.example.mangiaebasta.user.data.local.UserDao

@Database(entities = [User::class, Menu::class], version = 4)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun menuDao(): MenuDao
}
