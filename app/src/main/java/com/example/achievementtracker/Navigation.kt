package com.example.achievementtracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigator(
    navController: NavHostController,
    games: List<GameInfo>,
    onGameListChange: (List<GameInfo>) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "drawer_screen"
    ) {
        composable("drawer_screen") {
            DrawerScreen(
                navController = navController,
                games = games,
                onGameListChange = onGameListChange
            )
        }
        composable("welcome_screen") {
            WelcomeScreenContent(navController = navController)
        }
        composable("achievement_screen/{appId}") { backStackEntry ->
            val appId = backStackEntry.arguments?.getString("appId") ?: ""
            AchievementScreen(appId = appId, navController = navController)
        }

    }
}


