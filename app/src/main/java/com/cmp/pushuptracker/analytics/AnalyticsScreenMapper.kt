package com.cmp.pushuptracker.analytics

import com.cmp.pushuptracker.ui.navigationUtils.Screen

fun analyticsNameForRoute(route: String): String = when (route) {
    Screen.Home.route -> Screen.Home.title
    Screen.History.route -> Screen.History.title
    Screen.Profile.route -> Screen.Profile.title
    Screen.StartWorkout.route -> Screen.StartWorkout.title
    Screen.LivePreviewScreen.route -> Screen.LivePreviewScreen.title
    Screen.ThemeChangeView.route -> Screen.ThemeChangeView.title
    Screen.OnBoardingOne.route -> Screen.OnBoardingOne.title
    Screen.OnBoardingTwo.route -> Screen.OnBoardingTwo.title
    else -> route
}
