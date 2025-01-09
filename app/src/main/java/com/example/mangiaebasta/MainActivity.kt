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
import androidx.room.Room
import com.example.mangiaebasta.core.SharedPreferencesUtils
import com.example.mangiaebasta.core.data.Database
import com.example.mangiaebasta.ui.theme.MangiaEBastaTheme
import com.example.mangiaebasta.ui.theme.navbar.BottomNavigationBar
import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import com.example.mangiaebasta.user.data.repository.UserRepository
import com.example.mangiaebasta.user.presentation.UserViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MangiaEBastaTheme {
                val context = LocalContext.current
                val userDatabase =
                    Room
                        .databaseBuilder(
                            context,
                            Database::class.java,
                            "user_database",
                        ).build()

                val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
                val userDao = userDatabase.userDao()
                val userRepository = remember { UserRepository(userDao, context, ioDispatcher) }
                val navController = rememberNavController()

                HandleSID(context, ioDispatcher)
                val userUid = SharedPreferencesUtils.getStoredUID(LocalContext.current)?.toInt() ?: 0
                RestoreLastVisitedPage(context, navController)

                BottomNavigationBar(navController, context, userUid, UserViewModel(userRepository))
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    private fun HandleSID(
        context: Context,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        LaunchedEffect(Unit) {
            if (SharedPreferencesUtils.getStoredSID(context) == null) {
                lifecycleScope.launch(ioDispatcher) {
                    val userResponse = UserRemoteDataSource(context, ioDispatcher).requestSID()
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
