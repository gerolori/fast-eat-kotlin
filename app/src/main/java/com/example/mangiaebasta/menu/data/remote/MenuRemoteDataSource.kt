package com.example.mangiaebasta.menu.data.remote

import android.content.Context
import android.util.Log
import com.example.mangiaebasta.core.Constants
import com.example.mangiaebasta.core.SharedUtils
import com.example.mangiaebasta.menu.domain.model.ImageResponse
import com.example.mangiaebasta.menu.domain.model.MenuDetailedResponse
import com.example.mangiaebasta.menu.domain.model.MenuNearRequest
import com.example.mangiaebasta.menu.domain.model.MenuResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

class MenuRemoteDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val client =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    var sid = SharedUtils.getStoredSID(context)

    private fun updateCredentials() {
        sid = SharedUtils.getStoredSID(context)
    }

    suspend fun getMenuList(request: MenuNearRequest): List<MenuResponse> {
        val urlString = "${Constants.BASE_URL}/menu?lat=${request.lat}&lng=${request.lng}&sid=$sid"
        val response = client.get(urlString)
        return if (response.status.value != 200) {
            Log.e("MenuRemoteDataSource - getNearMenu", "Error: ${response.status.value}")
            emptyList()
        } else {
            Log.d("MenuRemoteDataSource - getNearMenu", "Found menus: ${response.body<List<MenuResponse>>()}")
            response.body<List<MenuResponse>>()
        }
    }

    suspend fun getMenuImage(mid: Int): ImageResponse {
        updateCredentials()
        val response = client.get("${Constants.BASE_URL}/menu/$mid/image?sid=$sid")
        return if (response.status.value != 200) {
            Log.e("MenuRemoteDataSource - getMenuImage", "Error: ${response.status.value}")
            ImageResponse("")
        } else {
            response.body<ImageResponse>()
        }
    }

    suspend fun getMenuDetailed(
        mid: Int,
        lat: Double,
        lng: Double,
    ): MenuDetailedResponse? {
        updateCredentials()
        val urlString = "${Constants.BASE_URL}/menu/$mid?lat=$lat&lng=$lng&sid=$sid"
        val response = client.get(urlString)
        return if (response.status.value != 200) {
            Log.e("MenuRemoteDataSource - getMenuDetailed", "Error: ${response.status.value}")
            response.body()
        } else {
            response.body<MenuDetailedResponse>()
        }
    }
}
