package com.example.mangiaebasta.common.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mangiaebasta.common.model.UserResponse
import kotlinx.coroutines.flow.first


class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    private val SID = stringPreferencesKey("sid")
    private val UID = intPreferencesKey("uid")
    private val LAST_SCREEN = stringPreferencesKey("last_screen")
    private val LAST_MID = intPreferencesKey("last_mid")

    suspend fun saveUser(userResponse: UserResponse) {

        dataStore.let {
            it.edit { preferences ->

                preferences[SID] = userResponse.sid
                preferences[UID] = userResponse.uid
            }
        }
    }

    suspend fun getUser(): UserResponse? {
        val preferences = dataStore.data.first()
        val sid = preferences[SID]
        Log.d("DataStoreManager", "SID: $sid")
        val uid = preferences[UID]
        Log.d("DataStoreManager", "UID: $uid")
        if (sid != null && uid != null) {
            Log.d("DataStoreManager", UserResponse(sid, uid).toString())
            return UserResponse(sid, uid)
        } else {
            return null
        }
    }

    suspend fun saveLastScreen(lastScreen: String?) {
        dataStore.let {
            it.edit { preferences ->
                if (lastScreen != null) {
                    preferences[LAST_SCREEN] = lastScreen
                }
            }
        }
    }

    suspend fun getLastScreen(): String {
        val preferences = dataStore.data.first()
        val lastScreen = preferences[LAST_SCREEN]
        Log.d("DataStoreManager", "LastScreen: $lastScreen")
        if (lastScreen != null) {
            return lastScreen
        } else {
            return "home"
        }
    }

    suspend fun saveLastMid(mid: Int?) {
        dataStore.let {
            it.edit { preferences ->
                if (mid != null) {
                    preferences[LAST_MID] = mid
                }

            }
        }
    }

    suspend fun getLastMid(): Int {
        val preferences = dataStore.data.first()
        val lastMid = preferences[LAST_MID]
        Log.d("DataStoreManager", "LastMid: $lastMid")
        if (lastMid != null) {
            return lastMid
        } else {
            return 0
        }
    }
}