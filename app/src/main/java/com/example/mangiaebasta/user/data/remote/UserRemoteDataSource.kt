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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

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
    private val sid = SharedPreferencesUtils.getStoredSID(context)
    private val uid = SharedPreferencesUtils.getStoredUID(context)

    suspend fun getUserInfo(): UserInfoResponse? =
        withContext(ioDispatcher) {
            try {
                val urlString = "${Constants.BASE_URL}/user/$uid?sid=$sid"
                val response =
                    client.get(urlString) {
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

    suspend fun updateUserInfo(
        uid: Int,
        updateUserRequest: UpdateUserRequest,
    ): UserInfoResponse? =
        withContext(ioDispatcher) {
            try {
                val urlString = "${Constants.BASE_URL}/user/$uid"
                val response =
                    client.put(urlString) {
                        header("Authorization", "Bearer ${updateUserRequest.sid}")
                        contentType(ContentType.Application.Json)
                        setBody(updateUserRequest)
                    }
                if (response.status != HttpStatusCode.OK) {
                    val error: ResponseError = response.body()
                    Log.d("UserNetworkDataSource", error.message)
                    null
                } else {
                    response.body<UserInfoResponse>()
                }
            } catch (e: Exception) {
                Log.e("UserNetworkDataSource", "Error updating user info", e)
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
