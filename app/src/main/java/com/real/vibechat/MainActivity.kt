package com.real.vibechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Route.AppRoute.route
                    ) {
                        authGraph(navController, innerPadding)
                        appGraph(
                            navController,
                            startDestination = AppScreen.Splash.route,
                            innerPadding
                        )
                    }
                }
            }
        }
    }
}