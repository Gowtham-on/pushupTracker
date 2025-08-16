package com.cmp.pushuptracker.ui.screen.onBoarding

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmp.pushuptracker.PushUpAppNavigation
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.navigationUtils.Screen.OnBoardingOne
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.cmp.pushuptracker.viewmodel.UtilViewmodel
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnBoardingNavigation(utilViewmodel: UtilViewmodel, userViewmodel: UserViewmodel) {
    val onBoardingNavController = rememberNavController()

    AnimatedNavHost(
        contentAlignment = Alignment.TopCenter,
        navController = onBoardingNavController,
        startDestination = OnBoardingOne.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            )
        }
    ) {
        composable(OnBoardingOne.route) {
            OnBoardingScreen1(utilViewmodel, userViewmodel, onBoardingNavController)
        }
        composable(Screen.OnBoardingTwo.route) {
            OnBoardingScreen2(utilViewmodel, userViewmodel, onBoardingNavController)
        }
        composable (Screen.Home.route) {
            PushUpAppNavigation(utilViewmodel, userViewmodel)
        }
    }
}