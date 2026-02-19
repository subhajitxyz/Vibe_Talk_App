package com.real.vibechat.navigation

import android.net.Uri

sealed class Route(val route: String) {
    object AppRoute: Route("AppRoute")
    object AuthRoute: Route("AuthRoute")
}

sealed class AuthScreen(val route: String) {
    object AuthPhoneNum: AuthScreen("AuthPhoneNumScreen")
    object AuthOtp: AuthScreen("AuthOtpScreen")

}

sealed class AppScreen(val route: String) {
    object Splash: AppScreen("SplashScreen")
    object Onboarding: AppScreen("OnboardingScreen")
    object MainScreen: AppScreen("MainScreen")
    object Story : AppScreen("story?imageUrl={imageUrl}&videoUrl={videoUrl}") {

        fun createRoute(imageUrl: String?, videoUrl: String?): String {
            return buildString {
                append("story?")
                append("imageUrl=${Uri.encode(imageUrl ?: "")}")
                append("&videoUrl=${Uri.encode(videoUrl ?: "")}")
            }
        }
    }


    object Explore: AppScreen("ExploreScreen")
    object Chats: AppScreen("ChatsScreen")
    object Profile: AppScreen("ProfileScreen")
}