package com.example.mangiaebasta.old.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun encodeToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeFromBase64(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    // Function to ensure the Bitmap is square
    fun makeSquare(bitmap: Bitmap): Bitmap {
        val dimension = Math.min(bitmap.width, bitmap.height)
        return Bitmap.createBitmap(bitmap, 0, 0, dimension, dimension)
    }

    // Function to return boolean if image contains html prefix
    fun isValidImage(input: String): Boolean {
        val htmlPrefixes = listOf("<!DOCTYPE html>", "<html>", "<head>", "<body>")
        return htmlPrefixes.any { input.trimStart().startsWith(it, ignoreCase = true) }
    }
}
