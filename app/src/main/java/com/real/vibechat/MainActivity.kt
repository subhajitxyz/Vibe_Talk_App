package com.real.vibechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.navigation.Route
import com.real.vibechat.navigation.appGraph
import com.real.vibechat.navigation.authGraph
import com.real.vibechat.ui.theme.VibeChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            VibeChatTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    intent?.data?.let { uri ->
                        if (uri.scheme == "vibechat" && uri.host == "chat") {

                            val userId = uri.lastPathSegment ?: return@let

                            // Clear entire navigation stack
                            navController.navigate(
                                AppScreen.ChatRoomScreen.createRoute(userId)
                            ) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Route.AppRoute.route,

                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
                ) {
                    authGraph(navController)
                    appGraph(
                        navController,
                        startDestination = AppScreen.Splash.route
                    )
                }
            }
        }
    }
}