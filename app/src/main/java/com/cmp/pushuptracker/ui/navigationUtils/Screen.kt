package com.cmp.pushuptracker.ui.navigationUtils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object History : Screen("history", "History", Icons.Filled.History)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object QuickAdd : Screen("quickAdd", "Quick Add")
    object StartWorkout : Screen("startWorkout", "Start Workout")
    object ThemeChangeView : Screen("themeChangeView", "Theme")
}