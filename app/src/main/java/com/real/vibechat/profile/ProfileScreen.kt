package com.real.vibechat.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Screen")
    }
}