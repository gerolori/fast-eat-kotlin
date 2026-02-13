package com.example.mangiaebasta.features.menu.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.room.Room
import com.example.mangiaebasta.common.data.local.AppDataBase
import com.example.mangiaebasta.common.data.remote.CommunicationController
import com.example.mangiaebasta.common.model.DeliveryLocationWithSid
import com.example.mangiaebasta.common.model.ImageDB
import com.example.mangiaebasta.common.model.Position

class ImageRepository(private val context: Context) {

    private val dao: ImagesDao

    init {
        val database = Room.databaseBuilder(
            context.applicationContext,
            AppDataBase::class.java,
            "images_database"
        ).build()
        dao = database.imagesDao()
    }

    suspend fun getImage(mid: Int, sid: String): Bitmap? {
        try {

            val sidWithDeliveryLocation = DeliveryLocationWithSid(sid, Position(0.0, 0.0))

            val menu = CommunicationController.getMenu(mid, sidWithDeliveryLocation)

            // Prova a recuperare l'immagine del menu dal database
            val imageFromDB = dao.getImageFromDB(mid)
            Log.d("ImageRepository", "$imageFromDB")
            // Se l'immagine esiste nel database
            if (imageFromDB != null) {
                // Controlla se l'immagine nel database è aggiornata
                Log.d("MainActivity", "${imageFromDB.imageVersion < menu.imageVersion}")
                if (imageFromDB.imageVersion < menu.imageVersion) {
                    // Richiedi una nuova immagine al server se non è aggiornata
                    val imageFromServer = CommunicationController.getMenuImage(mid, sid)

                    if (imageFromServer != null) {
                        // Salva la nuova immagine nel database
                        dao.insertImage(ImageDB(mid, menu.imageVersion, imageFromServer.base64))
                        return base64ToImage(imageFromServer.base64)
                    }
                } else {
                    // Restituisci l'immagine dal database se è aggiornata
                    return base64ToImage(imageFromDB.base64)
                }

            } else {
                // Se l'immagine non esiste nel database, richiedila al server
                val imageFromServer = CommunicationController.getMenuImage(mid, sid)
                Log.d("MainActivity", "${imageFromServer}")
                if (imageFromServer != null) {
                    // Salva l'immagine recuperata nel database
                    dao.insertImage(ImageDB(mid, menu.imageVersion, imageFromServer.base64))
                    return base64ToImage(imageFromServer.base64)
                }
            }
            return null
        } catch (e: Exception) {
            // Gestisci eventuali eccezioni
            Log.d("MainActivity", "Errore durante il recupero dell'immagine: ${e.message}")
            return null
        }
    }

    fun base64ToImage(base64String: String): Bitmap? {
        return try {
            // Decodifica la stringa Base64 in un array di byte
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            Log.d("MainActivity", "${decodedBytes}")
            // Converti i byte in un Bitmap
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            println("Errore nella decodifica Base64: ${e.message}")
            null
        }
    }

}