package com.example.mangiaebasta.user.data.remote

import android.content.Context
import android.util.Log
import com.example.mangiaebasta.core.Constants
import com.example.mangiaebasta.core.SharedPreferencesUtils
import com.example.mangiaebasta.core.domain.model.ResponseError
import com.example.mangiaebasta.user.domain.model.UpdateUserRequest
import com.example.mangiaebasta.user.domain.model.UserInfoResponse
import com.example.mangiaebasta.user.domain.model.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserRemoteDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val client =
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    private var sid = SharedPreferencesUtils.getStoredSID(context)
    private var uid = SharedPreferencesUtils.getStoredUID(context)

    private fun updateCredentials() {
        sid = SharedPreferencesUtils.getStoredSID(context)
        uid = SharedPreferencesUtils.getStoredUID(context)
    }

    suspend fun getUserInfo(): UserInfoResponse? =
        withContext(ioDispatcher) {
            if (sid == null) updateCredentials()
            try {
                val urlString = "${Constants.BASE_URL}/user/$uid?sid=$sid"
                val response =
                    client.get(urlString) {
                    }
                if (response.status.value != 200) {
                    val error: ResponseError = response.body()
                    Log.d("UserNetworkDataSource - getUser", error.message + uid.toString() + ": " + sid)
                    null
                } else {
                    response.body<UserInfoResponse>()
                }
            } catch (e: Exception) {
                Log.e("UserNetworkDataSource", "Error getting user info", e)
                null
            }
        }

    suspend fun updateUserInfo(
        uid: Int,
        updateUserRequest: UpdateUserRequest,
    ) = withContext(ioDispatcher) {
        if (sid == null) updateCredentials()
        try {
            val json = Json.encodeToJsonElement(UpdateUserRequest.serializer(), updateUserRequest) as JsonObject
            val jsonObjectWithSid =
                buildJsonObject {
                    json.forEach { (key, value) -> put(key, value) }
                    put("sid", sid ?: "")
                }

            val urlString = "${Constants.BASE_URL}/user/$uid"
            val response =
                client.put(urlString) {
                    contentType(ContentType.Application.Json)
                    setBody(jsonObjectWithSid)
                }

            if (response.status.value != 204) {
                val error: ResponseError = response.body()
                Log.d("UserNetworkDataSource - updateUser", error.message)
            } else {
                Log.d("UserNetworkDataSource - updateUser", "User info updated")
            }
        } catch (e: Exception) {
            Log.e("UserNetworkDataSource", "Error updating user info", e)
        }
    }

    suspend fun requestSID(): UserResponse? =
        withContext(ioDispatcher) {
            if (sid == null) updateCredentials()
            try {
                val urlString = "${Constants.BASE_URL}/user"
                val response =
                    client.post(urlString) {
                        contentType(ContentType.Application.Json)
                    }
                if (response.status.value != 200) {
                    val error: ResponseError = response.body()
                    Log.d("CommunicationController - requestSID", error.message)
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
