package com.example.mangiaebasta.menu.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // NOSONAR
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
import androidx.compose.runtime.getValue // NOSONAR
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue // NOSONAR
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangiaebasta.core.ImageUtils
import com.example.mangiaebasta.core.theme.MangiaEBastaTheme
import com.example.mangiaebasta.menu.data.local.Menu

@Suppress("ktlint:standard:function-naming")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MenuScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = viewModel(),
) {
    val menuList by menuViewModel.menuList.observeAsState(emptyList())
    val lat = 41.8992
    val lng = 12.4731
    var selectedMenu by remember { mutableStateOf<Menu?>(null) }

    LaunchedEffect(lat, lng) {
        Log.d("MenuScreen", "Getting menu list")
        menuViewModel.getMenuList(lat, lng)
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
                                            menuViewModel.getMenuById(menu.mid)
                                            selectedMenu = menu
                                        },
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            selectedMenu?.let { menu ->
                MenuDetailModal(
                    menu = menu,
                    onDismiss = { selectedMenu = null },
                    onBuyClick = { /* Handle buy action */ },
                )
            }
        }
    }
}

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

@OptIn(
    ExperimentalMaterial3Api::class,
)@Composable
fun MenuDetailModal(
    menu: Menu,
    onDismiss: () -> Unit,
    onBuyClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = {}, indication = null, interactionSource = remember { MutableInteractionSource() }),
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
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                bitmap = ImageUtils.decodeFromBase64(menu.image).asImageBitmap(),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .aspectRatio(1f)
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
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "€${menu.price}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "ETA: ${menu.deliveryTime} minutes",
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
