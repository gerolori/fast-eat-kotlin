package com.example.mangiaebasta

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.room.Room
import com.example.mangiaebasta.core.SharedUtils
import com.example.mangiaebasta.core.data.Database
import com.example.mangiaebasta.core.theme.MangiaEBastaTheme
import com.example.mangiaebasta.menu.data.repository.MenuRepository
import com.example.mangiaebasta.menu.presentation.MenuDetailedScreen
import com.example.mangiaebasta.menu.presentation.MenuScreen
import com.example.mangiaebasta.menu.presentation.MenuViewModel
import com.example.mangiaebasta.order.presentation.OrdersScreen
import com.example.mangiaebasta.user.data.remote.UserRemoteDataSource
import com.example.mangiaebasta.user.data.repository.UserRepository
import com.example.mangiaebasta.user.presentation.ProfileEditScreen
import com.example.mangiaebasta.user.presentation.ProfileScreen
import com.example.mangiaebasta.user.presentation.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val context: Context by lazy { applicationContext }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val db: Database by lazy {
        Room
            .databaseBuilder(context, Database::class.java, "database")
            .fallbackToDestructiveMigration()
            .build()
    }
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val userDao by lazy { this.db.userDao() }
    private val menuDao by lazy { this.db.menuDao() }
    private val userRepository: UserRepository by lazy { UserRepository(userDao, context, ioDispatcher) }
    private val menuRepository: MenuRepository by lazy { MenuRepository(menuDao, context, ioDispatcher) }

    private val menuViewModel: MenuViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = MenuViewModel(menuRepository, fusedLocationClient) as T
            },
        )[MenuViewModel::class.java]
    }

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = UserViewModel(userRepository) as T
            },
        )[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        setContent {
            MangiaEBastaTheme {
                val isSidRetrieved = remember { mutableStateOf(false) }

                HandleSID(context, ioDispatcher) {
                    isSidRetrieved.value = true
                }

//                val navController = rememberNavController()
                if (isSidRetrieved.value) {
                    val userUid = SharedUtils.getStoredUID(LocalContext.current) ?: 0
                    UserViewModel(userRepository).initializeUser(userUid)
//                    RestoreLastVisitedPage(context, navController)

                    Root(
                        userUid,
                        menuViewModel,
                        userViewModel,
                    )
//                    BottomNavigationBar(
//                        navController,
//                        context,
//                        userUid,
//                        userViewModel,
//                        menuViewModel,
//                    )
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun Root(
    userId: Int,
    menuViewModel: MenuViewModel,
    profileViewModel: UserViewModel,
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected =
                        currentRoute?.startsWith("menu") == true ||
                            currentRoute?.startsWith(
                                "confirm_order",
                            ) == true ||
                            currentRoute?.startsWith("home") == true,
                    onClick = { navController.navigate("home_stack") },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Menu") },
                )

                NavigationBarItem(
                    selected = currentRoute == "order",
                    onClick = { navController.navigate("order") },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Order",
                        )
                    },
                    label = { Text("Order") },
                )

                NavigationBarItem(
                    selected = currentRoute?.startsWith("profile") == true,
                    onClick = { navController.navigate("profile_stack") },
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                        )
                    },
                    label = { Text("Profile") },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_stack",
            modifier = Modifier.padding(innerPadding),
        ) {
            // Home Stack
            navigation(startDestination = "home", route = "home_stack") {
                composable("home") { MenuScreen(navController, menuViewModel) }
                composable("menu") { MenuDetailedScreen(navController, menuViewModel) }
            }

            // Order Page
            composable("order") { OrdersScreen(menuViewModel) }

            // Profile Stack

            navigation(startDestination = "profile", route = "profile_stack") {
                composable("profile") { ProfileScreen(navController, userId, profileViewModel) }
                composable("profile_form") { ProfileEditScreen() }
            }
        }
    }
}
