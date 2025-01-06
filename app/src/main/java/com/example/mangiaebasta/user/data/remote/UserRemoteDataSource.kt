package com.example.mangiaebasta.user.data.remote

import android.util.Log
import com.example.mangiaebasta.core.Constants
import com.example.mangiaebasta.core.domain.model.ResponseError
import com.example.mangiaebasta.user.domain.model.UserInfoResponse
import com.example.mangiaebasta.user.domain.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UserRemoteDataSource(
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val client =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    suspend fun getUserInfo(
        sid: String,
        uid: Int,
    ): UserInfoResponse? =
        withContext(ioDispatcher) {
            try {
                val urlString = "${Constants.BASE_URL}/user/$uid"
                val response =
                    client.get(urlString) {
                        header("Authorization", "Bearer $sid")
                    }
                if (response.status.value != 200) {
                    val error: ResponseError = response.body()
                    Log.d("UserNetworkDataSource", error.message)
                    null
                } else {
                    response.body<UserInfoResponse>()
                }
            } catch (e: Exception) {
                Log.e("UserNetworkDataSource", "Error getting user info", e)
                null
            }
        }

    suspend fun requestSID(): UserResponse? =
        withContext(ioDispatcher) {
            try {
                val urlString = "${Constants.BASE_URL}/user"
                val response =
                    client.post(urlString) {
                        contentType(ContentType.Application.Json)
                    }
                if (response.status.value != 200) {
                    val error: ResponseError = response.body()
                    Log.d("CommunicationController", error.message)
                    null
                } else {
                    response.body<UserResponse>()
                }
            } catch (e: Exception) {
                Log.e("CommunicationController", "Error requesting SID", e)
                null
            }
        }
}
