package com.real.vibechat.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(
    modifier: Modifier,
    navController: NavController
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text("ONboarding Screen")
    }

}