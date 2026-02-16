package com.real.vibechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

                NavHost(
                    navController = navController,
                    startDestination = Route.AppRoute.route
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