package com.vibechat.social.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.vibechat.social.presentation.chat.ChatRoomScreen
import com.vibechat.social.presentation.home.MainScreen
import com.vibechat.social.presentation.onboarding.OnboardingScreen
import com.vibechat.social.presentation.splash.SplashScreen
import com.vibechat.social.presentation.story.StoryScreen

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