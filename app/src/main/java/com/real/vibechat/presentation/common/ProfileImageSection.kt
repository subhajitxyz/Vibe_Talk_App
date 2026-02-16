package com.real.vibechat.presentation.common

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.real.vibechat.R

@Composable
fun ProfileImageSection(
    selectedImageUri: Uri?,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(selectedImageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.image_placeholder_icon),
            error = painterResource(R.drawable.image_placeholder_icon)
        )


        IconButton(
            onClick = onEditClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp)
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )
        }
    }
}


