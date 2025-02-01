package com.example.mangiaebasta.old.menu.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mangiaebasta.old.core.ImageUtils
import com.example.mangiaebasta.old.core.theme.MangiaEBastaTheme
import com.example.mangiaebasta.old.menu.data.local.Menu

@Suppress("ktlint:standard:function-naming")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun MenuScreen(
    navController: NavHostController,
    viewModel: MenuViewModel = viewModel(),
) {
    val menuList by viewModel.menuList.observeAsState(emptyList())
    val latitude by viewModel.latitude.observeAsState()
    val longitude by viewModel.longitude.observeAsState()
    var selectedMenu by remember { mutableStateOf<Menu?>(null) }

    LaunchedEffect(Unit) {
        viewModel.updateLocation()
    }

    LaunchedEffect(latitude, longitude) {
        if (latitude != null && longitude != null) {
            viewModel.getMenuList(latitude!!, longitude!!)
        }
    }

    LaunchedEffect(selectedMenu) {
        selectedMenu?.let { menu ->
            viewModel.saveSelectedMenu(menu)
            navController.navigate("menu")
        }
    }

    MangiaEBastaTheme {
        Box {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                                .clip(MaterialTheme.shapes.large),
                    ) {
                        Column {
                            Text(
                                "Choose your menu",
                                style = MaterialTheme.typography.headlineLargeEmphasized,
                                modifier = Modifier.padding(vertical = 20.dp),
                            )
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(menuList) { menu ->
                                    MenuListItem(
                                        name = menu.name,
                                        description = menu.shortDescription,
                                        price = menu.price,
                                        eta = menu.deliveryTime,
                                        image = ImageUtils.decodeFromBase64(menu.image),
                                        onClick = {
                                            viewModel.getMenuById(menu.mid)
                                            selectedMenu = menu
                                            navController.navigate("menu")
                                        },
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MenuListItem(
    name: String,
    description: String,
    price: Double,
    eta: Int,
    image: Bitmap,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = "€$price",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "ETA $eta m",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun MenuDetailScreen(
    menu: Menu,
    onDismiss: () -> Unit,
    onBuyClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            }

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = ImageUtils.decodeFromBase64(menu.image).asImageBitmap(),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = menu.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    menu.longDescription?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Start).padding(horizontal = 30.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "€${menu.price}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "delivery in ${menu.deliveryTime} minutes",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            FloatingActionButton(
                onClick = onBuyClick,
            ) {
                Text("Buy")
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun MenuDetailedScreen(
    navController: NavHostController,
    menuViewModel: MenuViewModel,
) {
    val selectedMenuId by menuViewModel.selectedMenu.observeAsState()
    val menuList by menuViewModel.menuList.observeAsState(emptyList())

    val menu = menuList.find { it.mid == selectedMenuId }

    menu?.let {
        MenuDetailScreen(
            menu = it,
            onDismiss = { navController.popBackStack() },
            onBuyClick = {
                menuViewModel.buyMenu(
                    it.mid,
                    menuViewModel.longitude.value ?: 0.0,
                    menuViewModel.latitude.value ?: 0.0,
                )
            },
        )
    }
}
