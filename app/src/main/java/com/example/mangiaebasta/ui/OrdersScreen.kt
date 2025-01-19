package com.example.mangiaebasta.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.roomexample.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.tasks.await

@Composable
fun OrdersScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasPermission by rememberSaveable { mutableStateOf(false) }
    var locationText by rememberSaveable { mutableStateOf("Checking location permission") }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                Log.d("MainActivity", "Posso calcolare la posizione")
            } else {
                Log.d("MainActivity", "Caso in cui l'utente non ha dato i permessi")
            }
        }

    LaunchedEffect(Unit) {
        hasPermission = checkLocationPermission(context)
        if (hasPermission) {
            val task =
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token,
                )
            try {
                val location = task.await()
                locationText = "Lat: ${location.latitude}, Lon: ${location.longitude}"
            } catch (e: Exception) {
                locationText = "Errore: ${e.message}"
            }
        } else {
            locationText = "Sto richiedendo i permessi"
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val mapViewportState =
        rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(-74.0066, 40.7135))
                zoom(15.5)
            }
        }

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                puckBearingEnabled = true
                puckBearing = PuckBearing.HEADING
                enabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        val marker =
            rememberIconImage(key = R.drawable.ic_launcher_foreground, painter = painterResource(R.drawable.ic_launcher_foreground))

        PointAnnotation(point = Point.fromLngLat(9.230356563663163, 45.47690665907551)) {
            iconImage = marker
        }
    }
}

fun checkLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
