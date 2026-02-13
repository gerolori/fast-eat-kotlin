package com.example.mangiaebasta.features.menu.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mangiaebasta.common.model.MenuNearby
import com.example.mangiaebasta.common.model.NearMenuandImage
import com.example.mangiaebasta.common.model.Position
import com.example.roomexample.R

@Composable
fun MenuListItem(menu: NearMenuandImage, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                bitmap = menu.image.asImageBitmap(),
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
                    text = menu.nearMenu.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = menu.nearMenu.shortDescription,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "€${menu.nearMenu.price}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "ETA ${menu.nearMenu.deliveryTime} m",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMenuListItem() {
    val sampleMenu = MenuNearby(
        mid = 1,
        name = "Sample Menu",
        price = 9.99,
        location = Position(0.0, 0.0),
        imageVersion = 1,
        shortDescription = "This is a sample menu item.",
        deliveryTime = 30
    )

    val sampleImage: Bitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.menu_photo_example
    )

    val sampleMenuandImage = NearMenuandImage(
        nearMenu = sampleMenu,
        image = sampleImage
    )

    MenuListItem(menu = sampleMenuandImage)
}