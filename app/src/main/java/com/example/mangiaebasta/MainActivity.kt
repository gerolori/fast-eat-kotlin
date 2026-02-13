package com.example.mangiaebasta

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mangiaebasta.common.presentation.FirstRun
import com.example.mangiaebasta.common.presentation.Root
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen
import com.example.mangiaebasta.common.data.local.DataStoreManager
import com.example.mangiaebasta.common.utils.PositionManager
import com.example.mangiaebasta.features.menu.data.ImageRepository
import com.example.mangiaebasta.common.presentation.theme.MangiaEBastaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {

    val factory = viewModelFactory {
        initializer {
            val datastoreManager = DataStoreManager(dataStore = dataStore)
            val positionManager = PositionManager(this@MainActivity)
            val imageRepo = ImageRepository(this@MainActivity)
            AppViewModel(datastoreManager, positionManager, imageRepo)
        }
    }
    val appViewModel: AppViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MangiaEBastaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(appViewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appViewModel.saveDataDS()
    }
}

@Composable
fun MyApp(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    val firstRun = appViewModel.firstRun.collectAsState().value
    var hasPermission: Boolean? by remember { mutableStateOf(null) }
    val position = appViewModel.position.collectAsState().value
    val user = appViewModel.user.collectAsState().value

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        CoroutineScope(Dispatchers.Main).launch {
            hasPermission = isGranted
            appViewModel.getLocation()
        }
        Log.d("MainActivity", "Permission granted: $isGranted")
    }

    LaunchedEffect(firstRun) {
        if (firstRun == null) {
            appViewModel.getFirstRun()
        } else if (firstRun == false) {
            hasPermission = appViewModel.checkLocationPermission()
            if (hasPermission == true) {
                appViewModel.getLocation()
            } else {
                Log.d("MainActivity", "Asking for location permission")
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            appViewModel.getUser()
            appViewModel.reloadData()
        }
    }

    when (firstRun) {
        null -> SplashLoadingScreen()
        true -> FirstRun(appViewModel, modifier)
        false -> {
            if (position != null && user != null) {
                Root(appViewModel)
            } else if (hasPermission == false && user != null) {
                RequestLocationPermissionScreen {
                    hasPermission = appViewModel.checkLocationPermission()
                }
            } else {
                SplashLoadingScreen()
            }
        }
    }
}

@Composable
fun RequestLocationPermissionScreen(onDoneClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Share your location to continue",
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDoneClick,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Done")
        }
    }
}