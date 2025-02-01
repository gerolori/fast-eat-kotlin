package com.example.mangiaebasta.old.menu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenu(menu: Menu)

    @Update
    suspend fun updateMenu(menu: Menu)

    @Query("SELECT * FROM menu WHERE mid = :mid")
    suspend fun getMenu(mid: Int): Menu?

    @Query("UPDATE menu SET image = :image, imageVersion = :imageVersion WHERE mid = :mid")
    suspend fun updateMenuImage(
        mid: Int,
        image: String,
        imageVersion: Int,
    )

    @Query("UPDATE menu SET longDescription = :longDescription WHERE mid = :mid")
    suspend fun updateMenuLongDescription(
        mid: Int,
        longDescription: String,
    )
}
