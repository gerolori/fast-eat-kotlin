package com.example.mangiaebasta.old.menu.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "order_prefs")

class OrderDataStore(
    private val context: Context,
) {
    private val gson = Gson()
    private val ORDER_KEY = stringPreferencesKey("order")

    val orderFlow: Flow<Order?> =
        context.dataStore.data
            .map { preferences ->
                preferences[ORDER_KEY]?.let { orderJson ->
                    gson.fromJson(orderJson, Order::class.java)
                }
            }

    suspend fun saveOrder(order: Order) {
        val orderJson = gson.toJson(order)
        context.dataStore.edit { preferences ->
            preferences[ORDER_KEY] = orderJson
        }
    }

    suspend fun clearOrder() {
        context.dataStore.edit { preferences ->
            preferences.remove(ORDER_KEY)
        }
    }
}
