package com.example.mangiaebasta.features.order.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.AppViewModel
import com.example.mangiaebasta.common.model.CompletedOrderResponse
import com.example.mangiaebasta.common.model.DeliveredOrderResponse
import com.example.mangiaebasta.common.model.MenuResponseFromGet
import com.example.mangiaebasta.common.model.Position
import com.example.mangiaebasta.common.presentation.SplashLoadingScreen
import com.example.roomexample.BuildConfig
import com.example.roomexample.R
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun OrderPage(appViewModel: AppViewModel) {
    val order by appViewModel.orderInfo.collectAsState()
    val userInfo by appViewModel.user.collectAsState()
    var menu by remember { mutableStateOf<MenuResponseFromGet?>(null) }
    val mapViewPortState = rememberMapViewportState()

    LaunchedEffect(Unit) {
        Log.d("OrderPage", "Initialized")
        appViewModel.getOrderInfo()
        appViewModel.setScreen("order")
    }

    LaunchedEffect(order) {
        if (order != null) {
            when (order) {
                is CompletedOrderResponse -> {
                    menu = appViewModel.getMenu((order as CompletedOrderResponse).mid)
                    mapViewPortState.setCameraOptions {
                        center(
                            Point.fromLngLat(
                                (order as CompletedOrderResponse).deliveryLocation.lng,
                                (order as CompletedOrderResponse).deliveryLocation.lat
                            )
                        )
                        zoom(15.0)
                    }
                }

                is DeliveredOrderResponse -> {
                    menu = appViewModel.getMenu((order as DeliveredOrderResponse).mid)
                    mapViewPortState.setCameraOptions {
                        center(
                            Point.fromLngLat(
                                (order as DeliveredOrderResponse).currentPosition.lng,
                                (order as DeliveredOrderResponse).currentPosition.lat
                            )
                        )
                        zoom(15.0)
                    }
                    delay(5000)
                    appViewModel.getOrderInfo()
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        when {
            order is CompletedOrderResponse && menu != null -> CompletedOrderContent(
                order as CompletedOrderResponse, menu!!, mapViewPortState
            )

            order is DeliveredOrderResponse && menu != null -> OnDeliveryOrderContent(
                order as DeliveredOrderResponse, menu!!, mapViewPortState
            )

            userInfo != null && order == null -> NoOrder()
            else -> SplashLoadingScreen()
        }
    }
}

@Composable
fun NoOrder() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No order yet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Go to Menu to place your order")
    }
}

@Composable
fun CompletedOrderContent(
    order: CompletedOrderResponse, menu: MenuResponseFromGet, mapViewPortState: MapViewportState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Last Order #${order.oid}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OrderSummary(order, menu)
        Spacer(modifier = Modifier.height(16.dp))
        MapComponent(mapViewPortState, order.deliveryLocation, menu.location)
    }
}

@Composable
fun OnDeliveryOrderContent(
    order: DeliveredOrderResponse, menu: MenuResponseFromGet, mapViewPortState: MapViewportState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Last Order #${order.oid}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OrderSummary(order, menu)
        Spacer(modifier = Modifier.height(16.dp))
        MapComponent(mapViewPortState, order.deliveryLocation, menu.location, order.currentPosition)
    }
}

fun formatTimestamp(timestamp: String): String {
    val instant = Instant.parse(timestamp)
    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    return instant.atZone(zoneId).format(formatter)
}

@Composable
fun OrderSummary(order: Any, menu: MenuResponseFromGet) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Name: ${menu.name}", style = MaterialTheme.typography.headlineSmall)
        when (order) {
            is CompletedOrderResponse -> {
                Text(
                    "Delivered at: ${formatTimestamp(order.deliveryTimestamp)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            is DeliveredOrderResponse -> {
                Text(
                    "ETA: ${formatTimestamp(order.expectedDeliveryTimestamp)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MapComponent(
    mapViewPortState: MapViewportState,
    deliveryLocation: Position,
    shopLocation: Position,
    currentPosition: Position? = null
) {
    MapboxMap(
        modifier = Modifier
            .fillMaxSize(),
        mapViewportState = mapViewPortState,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.mapboxMap.loadStyle(BuildConfig.MAPBOX_STYLE_URL)
        }

        val homeIcon = rememberIconImage(
            key = R.drawable.home,
            painter = painterResource(id = R.drawable.home)
        )
        val pinIcon =
            rememberIconImage(key = R.drawable.pin, painter = painterResource(id = R.drawable.pin))

        PointAnnotation(point = Point.fromLngLat(deliveryLocation.lng, deliveryLocation.lat)) {
            iconImage = homeIcon
            iconSize = 0.2
        }

        PointAnnotation(point = Point.fromLngLat(shopLocation.lng, shopLocation.lat)) {
            iconImage = pinIcon
            iconSize = 0.2
        }

        if (currentPosition != null) {
            val droneIcon = rememberIconImage(
                key = R.drawable.drone_200, painter = painterResource(id = R.drawable.drone_200)
            )
            PointAnnotation(point = Point.fromLngLat(currentPosition.lng, currentPosition.lat)) {
                iconImage = droneIcon
                iconSize = 0.3
            }
        }
    }
}

