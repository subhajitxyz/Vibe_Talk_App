package com.vibechat.social.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vibechat.social.presentation.auth.AuthOtpScreen
import com.vibechat.social.presentation.auth.AuthPhoneNumberScreen
import com.vibechat.social.presentation.auth.AuthSharedViewModel

fun NavGraphBuilder.authGraph(
    navController: NavController
) {
    navigation(
        startDestination = AuthScreen.AuthPhoneNum.route,
        route = Route.AuthRoute.route
    ) {
        composable(
            AuthScreen.AuthPhoneNum.route
        ) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
              navController.getBackStackEntry(Route.AuthRoute.route)
            }
            val viewModel: AuthSharedViewModel = hiltViewModel(parentEntry)

            AuthPhoneNumberScreen(
                Modifier
                    .fillMaxSize(),
                navController,
                viewModel
            )
        }

        composable(
            AuthScreen.AuthOtp.route
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Route.AuthRoute.route)
            }
            val viewModel: AuthSharedViewModel = hiltViewModel(parentEntry)

            AuthOtpScreen(
                Modifier
                    .fillMaxSize(),
                navController,
                viewModel
            )
        }
    }

}