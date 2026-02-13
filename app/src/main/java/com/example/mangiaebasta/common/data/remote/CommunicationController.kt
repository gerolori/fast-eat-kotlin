package com.example.mangiaebasta.common.data.remote

import android.net.Uri
import android.util.Log
import com.example.mangiaebasta.common.model.CompletedOrderResponse
import com.example.mangiaebasta.common.model.DeliveredOrderResponse
import com.example.mangiaebasta.common.model.DeliveryLocationWithSid
import com.example.mangiaebasta.common.model.MenuImageResponse
import com.example.mangiaebasta.common.model.MenuNearby
import com.example.mangiaebasta.common.model.MenuResponseFromGet
import com.example.mangiaebasta.common.model.OrderStatusResponse
import com.example.mangiaebasta.common.model.UpdateUserRequest
import com.example.mangiaebasta.common.model.UserInfoResponse
import com.example.mangiaebasta.common.model.UserResponse
import com.example.roomexample.BuildConfig

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object CommunicationController {

    private val BASE_URL = BuildConfig.API_BASE_URL
    private val TAG = " CommunicationController"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }


    suspend fun genericRequest(
        url: String, method: HttpMethod,
        queryParameters: Map<String, String> = emptyMap(),
        requestBody: Any? = null
    ): HttpResponse {

        val urlUri = Uri.parse(url)
        val urlBuilder = urlUri.buildUpon()
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value)
        }
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, completeUrlString)

        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        val result = when (method) {
            HttpMethod.GET -> client.get(completeUrlString, request)
            HttpMethod.POST -> client.post(completeUrlString, request)
            HttpMethod.DELETE -> client.delete(completeUrlString, request)
            HttpMethod.PUT -> client.put(completeUrlString, request)
        }
        return result
    }

    suspend fun createUser(): UserResponse {
        Log.d(TAG, "createUser")

        val url = BASE_URL + "/user"

        val httpResponse = genericRequest(url, HttpMethod.POST)
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nella creazione dell'utente: ${httpResponse.status}")
        }
        val result: UserResponse = httpResponse.body()
        return result
    }

    suspend fun getUserInfo(userResponse: UserResponse): UserInfoResponse {
        Log.d(TAG, "getUserInfo")

        val url = BASE_URL + "/user" + "/" + userResponse.uid

        val httpResponse =
            genericRequest(url, HttpMethod.GET, mapOf("sid" to userResponse.sid))
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nel reperimento dei dati dell'utente: ${httpResponse.status}")
        }
        val result: UserInfoResponse = httpResponse.body()
        return result
    }

    suspend fun updateUser(userForPut: UpdateUserRequest, uid: Int) {
        Log.d(TAG, "updateUser")

        val url = BASE_URL + "/user" + "/" + uid

        val httpResponse = genericRequest(url, HttpMethod.PUT, requestBody = userForPut)
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nell'aggiornamento dei dati dell'utente: ${httpResponse.status}")
        }

    }

    suspend fun createOrder(deliveryLocationWithSid: DeliveryLocationWithSid, mid: Int): Any {
        Log.d(TAG, "createOrder")

        val url = "$BASE_URL/menu/$mid/buy"

        try {
            val httpResponse =
                genericRequest(url, HttpMethod.POST, requestBody = deliveryLocationWithSid)

            if (httpResponse.status.value == 409) {
                Log.w(TAG, "User already has an active order")
                throw Exception("User already has an active order")
            }
            if (httpResponse.status.value == 403) {
                Log.w(TAG, "Invalid Card")
                throw Exception("Invalid card")
            }
            if (!httpResponse.status.isSuccess()) {
                throw Exception("Error creating order: ${httpResponse.status}")
            }

            val partialResult: OrderStatusResponse = httpResponse.body()
            return when (partialResult.status) {
                "ON_DELIVERY" -> httpResponse.body<DeliveredOrderResponse>()
                "COMPLETED" -> httpResponse.body<CompletedOrderResponse>()
                else -> throw Exception("Unexpected response type")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in createOrder", e)
            throw e
        }
    }

    suspend fun getOrder(oid: Int, sid: String): Any {
        Log.d(TAG, "getOrder")

        val url = BASE_URL + "/order" + "/" + oid

        val httpResponse = genericRequest(url, HttpMethod.GET, mapOf("sid" to sid))
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nel reperimento dei dati dell'ordine: ${httpResponse.status}")
        }
        val partialResult: OrderStatusResponse = httpResponse.body()
        if (partialResult.status == "ON_DELIVERY") {
            val result: DeliveredOrderResponse = httpResponse.body()
            return result
        } else {
            val result: CompletedOrderResponse = httpResponse.body()
            return result
        }

    }

    suspend fun getMenu(mid: Int, sid: DeliveryLocationWithSid): MenuResponseFromGet {
        Log.d(TAG, "getMenu")
        val url = BASE_URL + "/menu" + "/" + mid

        val httpResponse = genericRequest(
            url,
            HttpMethod.GET,
            mapOf(
                "sid" to sid.sid,
                "lat" to sid.deliveryLocation.lat.toString(),
                "lng" to sid.deliveryLocation.lng.toString()
            )
        )
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nel reperimento dei dati del menu: ${httpResponse.status}")
        }
        val result: MenuResponseFromGet = httpResponse.body()
        Log.d(TAG, "getMenu: $result")
        return result
    }

    suspend fun getMenus(sid: DeliveryLocationWithSid): List<MenuNearby> {

        Log.d(TAG, "getMenus")

        val url = BASE_URL + "/menu"

        val httpResponse = genericRequest(
            url,
            HttpMethod.GET,
            mapOf(
                "sid" to sid.sid,
                "lat" to sid.deliveryLocation.lat.toString(),
                "lng" to sid.deliveryLocation.lng.toString()
            )
        )
        if (!httpResponse.status.isSuccess()) {
            throw Exception("Errore nel reperimento dei dati dei menu: ${httpResponse.status}")
        }
        val result: List<MenuNearby> = httpResponse.body()
        Log.d(TAG, "getMenus: $result")
        return result
    }

    suspend fun getMenuImage(mid: Int, sid: String): MenuImageResponse? {
        Log.d(TAG, "getMenuImage: Starting request for menu $mid")
        val url = "$BASE_URL/menu/$mid/image"

        return try {
            val httpResponse = genericRequest(
                url,
                HttpMethod.GET,
                mapOf("sid" to sid)
            )

            Log.d(TAG, "getMenuImage: Received response with status ${httpResponse.status}")

            if (httpResponse.status.isSuccess()) {
                val result: MenuImageResponse = httpResponse.body()
                Log.d(
                    TAG,
                    "getMenuImage: Successfully parsed response. Image data length: ${result.base64.length}"
                )
                result
            } else {
                Log.e(TAG, "getMenuImage: Failed to retrieve image. Status: ${httpResponse.status}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMenuImage: Exception occurred", e)
            null
        }
    }


}

