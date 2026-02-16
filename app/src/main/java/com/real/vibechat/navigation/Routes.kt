package com.real.vibechat.navigation

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


    object Explore: AppScreen("ExploreScreen")
    object Chats: AppScreen("ChatsScreen")
    object Profile: AppScreen("ProfileScreen")
}