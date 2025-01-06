package com.example.mangiaebasta

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mangiaebasta.core.SharedPreferencesUtils
import com.example.mangiaebasta.ui.theme.MangiaEBastaTheme
import com.example.mangiaebasta.ui.theme.navbar.BottomNavigationBar
import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import com.example.mangiaebasta.user.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MangiaEBastaTheme {
                val context = LocalContext.current
                val userRepository = remember { UserRepository(UserRemoteDataSource(Dispatchers.IO)) }
                val navController = rememberNavController()

                HandleSID(context, userRepository)
                RestoreLastVisitedPage(context, navController)

                BottomNavigationBar(navController, context)
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    private fun HandleSID(
        context: Context,
        userRepository: UserRepository,
    ) {
        LaunchedEffect(Unit) {
            if (SharedPreferencesUtils.getStoredSID(context) == null) {
                lifecycleScope.launch {
                    val userResponse = userRepository.requestSID()
                    if (userResponse != null) {
                        SharedPreferencesUtils.storeAppPrefs(context, userResponse.sid, userResponse.uid)
                        Log.d("MainActivity", "First boot: SID and UID retrieved from server")
                    }
                }
            } else {
                Log.d("MainActivity", "SID already stored in SharedPreferences")
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    private fun RestoreLastVisitedPage(
        context: Context,
        navController: NavController,
    ) {
        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                val lastVisitedPage = SharedPreferencesUtils.getLastVisitedPage(context)
                if (lastVisitedPage != null) {
                    navController.navigate(lastVisitedPage)
                }
            }
        }
    }
}
