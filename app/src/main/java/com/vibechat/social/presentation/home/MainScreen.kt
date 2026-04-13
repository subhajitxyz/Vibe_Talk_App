package com.vibechat.social.presentation.home

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vibechat.social.presentation.chats.ChatsScreen
import com.vibechat.social.presentation.explore.ExploreScreen
import com.vibechat.social.navigation.AppScreen
import com.vibechat.social.presentation.profile.ProfileScreen
import com.vibechat.social.ui.theme.PrimaryLightColor

@Composable
fun MainScreen(
    modifier: Modifier,
    rootNavController: NavController
) {

    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(bottomNavController)
        }
    ) { innerPadding ->

        NavHost(
            navController = bottomNavController,
            startDestination = AppScreen.Chats.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Explore.route) {
                ExploreScreen(rootNavController)
            }
            composable(AppScreen.Chats.route) {
                ChatsScreen(rootNavController)
            }
            composable(AppScreen.Profile.route) {
                ProfileScreen(
                    rootNavController
                )
            }

        }
    }
}

@Composable
fun BottomBar(
    bottomNavController: NavHostController
) {
    val bottomNavItems = listOf(
        BottomNavItem("Explore", AppScreen.Explore.route, Icons.Default.Search),
        BottomNavItem("Chats", AppScreen.Chats.route, Icons.Default.Email),
        BottomNavItem("Profile", AppScreen.Profile.route, Icons.Default.Person)
    )

    val currentRoute = bottomNavController.currentBackStackEntryAsState()
        .value?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PrimaryLightColor
                ),
                onClick = {
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.startDestinationId) {
                        }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }

}
