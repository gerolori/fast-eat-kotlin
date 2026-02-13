package com.example.mangiaebasta.features.menu.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mangiaebasta.common.model.ImageDB

@Dao
interface ImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(ImageDB: ImageDB)

    @Query("SELECT * FROM images WHERE mid = :mid")
    suspend fun getImageFromDB(mid: Int): ImageDB?

}