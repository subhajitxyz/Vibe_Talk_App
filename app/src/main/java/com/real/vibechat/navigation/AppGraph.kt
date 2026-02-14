package com.real.vibechat.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.real.vibechat.presentation.home.HomeScreen
import com.real.vibechat.presentation.onboarding.OnboardingScreen
import com.real.vibechat.presentation.splash.SplashScreen

fun NavGraphBuilder.appGraph(
    navController: NavController,
    startDestination: String,
    paddingValues: PaddingValues
) {

    navigation(
        startDestination = startDestination,
        route = Route.AppRoute.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                navController
            )
        }
        composable(AppScreen.Onboarding.route) {
            OnboardingScreen(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                navController
            )
        }

        composable(AppScreen.HomeScreen.route) {
            HomeScreen(Modifier.fillMaxSize().padding(paddingValues))
        }
    }

}