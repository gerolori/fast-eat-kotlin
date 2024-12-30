package com.example.mangiaebasta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mangiaebasta.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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
    }

    // Save the last visited page in SharedPreferences
    fun saveLastVisitedPage(pageId: Int) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("last_visited_page", pageId)
        editor.apply()
    }

    // Retrieve the last visited page from SharedPreferences
    fun getLastVisitedPage(): Int? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val pageId = sharedPreferences.getInt("last_visited_page", -1)
        return if (pageId != -1) pageId else null
    }
}
