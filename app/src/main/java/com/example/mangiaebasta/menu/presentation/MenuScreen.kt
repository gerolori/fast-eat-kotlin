package com.example.mangiaebasta.menu.presentation

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items // NOSONAR
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue // NOSONAR
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangiaebasta.core.ImageUtils
import com.example.mangiaebasta.core.theme.MangiaEBastaTheme
import com.example.mangiaebasta.core.theme.OrangeGrey80

@Suppress("ktlint:standard:function-naming")
@Composable
fun MenuScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = viewModel(),
) {
    val menuList by menuViewModel.menuList.observeAsState(emptyList())

    val lat = 41.8992
    val lng = 12.4731
    LaunchedEffect(lat, lng) {
        Log.d("MenuScreen", "Getting menu list")
        menuViewModel.getMenuList(lat, lng)
    }

    MangiaEBastaTheme {
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
                            style = MaterialTheme.typography.titleLarge,
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
                                    image = ImageUtils.decodeFromBase64(menu.image), // Replace with actual image resource
                                    onClick = {
                                        Toast.makeText(navController.context, "More info about ${menu.name}", Toast.LENGTH_SHORT).show()
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
                .background(OrangeGrey80, RoundedCornerShape(8.dp))
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
                color = Color.Black,
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
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
                color = Color.Black,
            )
            Text(
                text = "ETA " + eta.toString() + "m",
                fontSize = 12.sp,
                color = Color.Gray,
            )
        }
    }
}
