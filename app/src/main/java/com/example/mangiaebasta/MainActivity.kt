@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.mangiaebasta

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mangiaebasta.core.Constants
import com.example.mangiaebasta.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_dashboard,
                ),
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Retrieve the last visited page and set it as the start destination
        val lastVisitedPage = getLastVisitedPage()
        if (lastVisitedPage != null) {
            val navInflater: NavInflater = navController.navInflater
            val navGraph: NavGraph = navInflater.inflate(R.navigation.mobile_navigation)
            navGraph.setStartDestination(lastVisitedPage)
            navController.graph = navGraph
        }

        // Add OnDestinationChangedListener to save the last visited page
        navController.addOnDestinationChangedListener { _, destination, _ ->
            saveLastVisitedPage(destination.id)
        }

        // Check if SID is stored, if not request it
        if (getStoredSID() == null) {
            CoroutineScope(Dispatchers.IO).launch {
                requestSID()
            }
        } else {
            Log.d("MainActivity - onCreate()", "SID loaded from shared preferences successfully")
        }
    }

    // Save the last visited page in SharedPreferences
    private fun saveLastVisitedPage(pageId: Int) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("last_visited_page", pageId)
        editor.apply()
    }

    // Retrieve the last visited page from SharedPreferences
    private fun getLastVisitedPage(): Int? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val pageId = sharedPreferences.getInt("last_visited_page", -1)
        return if (pageId != -1) pageId else null
    }

    // Retrieve the stored SID from SharedPreferences
    private fun getStoredSID(): String? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("sid", null)
    }

    // Request SID from the server
    private fun requestSID() {
        val clientReal =
            HttpClient(Android) {
                // Json serialization support
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                        },
                    )
                }
            }
        val urlString = "${Constants.BASE_URL}/user"
        CoroutineScope(Dispatchers.Main).launch {
            val response =
                clientReal.post(urlString) {
                    contentType(ContentType.Application.Json)
                }
            if (response.status.value != 200) {
                val error: ResponseError = response.body()
                Log.d("MainActivity", error.message)
            } else {
                val body: UserResponse = response.body()
                Log.d("MainActivity - requestSID", body.toString())
                storeSID(body.sid)
            }
        }
    }

    // Store the SID in SharedPreferences
    private fun storeSID(sid: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("sid", sid)
        editor.apply()
    }

    @Serializable
    data class UserResponse(
        val sid: String,
        val uid: Int,
    )

    @Serializable
    data class ResponseError(
        val message: String,
    )
}
