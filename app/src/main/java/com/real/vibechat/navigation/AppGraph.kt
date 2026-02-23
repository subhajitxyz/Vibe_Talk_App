package com.real.vibechat.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.real.vibechat.presentation.chat.ChatRoomScreen
import com.real.vibechat.presentation.home.MainScreen
import com.real.vibechat.presentation.onboarding.OnboardingScreen
import com.real.vibechat.presentation.splash.SplashScreen
import com.real.vibechat.presentation.story.StoryScreen

fun NavGraphBuilder.appGraph(
    navController: NavController,
    startDestination: String
) {

    navigation(
        startDestination = startDestination,
        route = Route.AppRoute.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(
                Modifier
                    .fillMaxSize(),
                navController
            )
        }
        composable(AppScreen.Onboarding.route) {
            OnboardingScreen(
                Modifier
                    .fillMaxSize(),
                navController
            )
        }

        // this will be main screen
        composable(AppScreen.MainScreen.route) {
            MainScreen(Modifier.fillMaxSize(), navController)
        }

        composable(
            route = AppScreen.Story.route,
            arguments = listOf(
                navArgument("imageUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("videoUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val imageUrl = backStackEntry.arguments?.getString("imageUrl")
            val videoUrl = backStackEntry.arguments?.getString("videoUrl")

            StoryScreen(
                imageUrl = imageUrl?.ifEmpty { null },
                videoUrl = videoUrl?.ifEmpty { null },
                rootNavController = navController
            )
        }

        composable(
            route = AppScreen.ChatRoomScreen.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "vibechat://chat/{userId}"
                }
            )
        ) {
            ChatRoomScreen(navController= navController)
        }
    }

}