package com.real.vibechat.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.real.vibechat.R
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.navigation.AuthScreen
import com.real.vibechat.presentation.onboarding.StartDestination
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun SplashScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 2000
            )
        )
    }

    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {

            StartDestination.AUTH -> {
                navController.navigate(AuthScreen.AuthPhoneNum.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            StartDestination.HOME -> {
                navController.navigate(AppScreen.MainScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            StartDestination.ONBOARDING -> {
                navController.navigate(AppScreen.Onboarding.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            null -> Unit
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.vibe_talk_icon),
                contentDescription = "Vibe Talk Splash Icon",
                modifier = Modifier.size(170.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
            )
            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = PrimaryColor,
                strokeWidth = 2.dp
            )
        }

    }
}
