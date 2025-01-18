package com.example.mangiaebasta

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mangiaebasta.core.SharedUtils
import com.example.mangiaebasta.core.data.Database
import com.example.mangiaebasta.menu.data.repository.MenuRepository
import com.example.mangiaebasta.menu.presentation.MenuViewModel
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

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        setContent {
            MangiaEBastaTheme {
                val context = LocalContext.current
                val userDatabase =
                    Room
                        .databaseBuilder(
                            context,
                            Database::class.java,
                            "user_database",
                        ).fallbackToDestructiveMigration()
                        .build()
                val menuDatabase =
                    Room
                        .databaseBuilder(
                            context,
                            Database::class.java,
                            "menu_database",
                        ).fallbackToDestructiveMigration()
                        .build()

                val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
                val userDao = userDatabase.userDao()
                val menuDao = menuDatabase.menuDao()
//                TODO: implement viewmodel factory for both user and menu viewmodels
                val userRepository = remember { UserRepository(userDao, context, ioDispatcher) }
                val menuRepository = remember { MenuRepository(menuDao, context, ioDispatcher) }
                val isSidRetrieved = remember { mutableStateOf(false) }

                HandleSID(context, ioDispatcher) {
                    isSidRetrieved.value = true
                }

                val navController = rememberNavController()
                if (isSidRetrieved.value) {
                    val userUid = SharedUtils.getStoredUID(LocalContext.current) ?: 0
                    UserViewModel(userRepository).initializeUser(userUid)
                    RestoreLastVisitedPage(context, navController)

                    BottomNavigationBar(
                        navController,
                        context,
                        userUid,
                        UserViewModel(userRepository),
                        MenuViewModel(menuRepository),
                    )
                }
            }
        }
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    private fun HandleSID(
        context: Context,
        ioDispatcher: CoroutineDispatcher = Dispatchers.Main,
        isSidRetrieved: () -> Unit,
    ) {
        LaunchedEffect(Unit) {
            if (SharedUtils.getStoredSID(context) == null) {
                lifecycleScope.launch(ioDispatcher) {
                    val userResponse = UserRemoteDataSource(context, ioDispatcher).requestSID()
                    if (userResponse != null) {
                        SharedUtils.storeAppPrefs(
                            context,
                            userResponse.sid,
                            userResponse.uid,
                        )
                        Log.d("MainActivity", "First boot: SID and UID retrieved from server")
                        isSidRetrieved()
                    }
                }
            } else {
                Log.d("MainActivity", "SID already stored in SharedPreferences")
                isSidRetrieved()
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
                val lastVisitedPage = SharedUtils.getLastVisitedPage(context)
                if (lastVisitedPage != null) {
                    navController.navigate(lastVisitedPage)
                }
            }
        }
    }
}
