package com.cmp.pushuptracker.ui.navigationUtils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object History : Screen("Statistics", "Statistics", Icons.Filled.BarChart)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object StartWorkout : Screen("startWorkout", "Start Workout")
    object LivePreviewScreen : Screen("livePreviewScreen", "Live Preview")
    object ThemeChangeView : Screen("themeChangeView", "Theme")
    object OnBoardingOne : Screen("onBoardingOne", "OnBoardingOne")
    object OnBoardingTwo : Screen("onBoardingTwo", "OnBoardingTwo")
    object Paywall : Screen("paywall", "Pushup Plus")
}
