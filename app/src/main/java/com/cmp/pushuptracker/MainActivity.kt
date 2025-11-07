package com.cmp.pushuptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmp.pushuptracker.analytics.AnalyticsLogger
import com.cmp.pushuptracker.analytics.analyticsNameForRoute
import com.cmp.pushuptracker.ui.components.HomeScreenShimmer
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.screen.history.HistoryScreen
import com.cmp.pushuptracker.ui.screen.home.HomeScreen
import com.cmp.pushuptracker.ui.screen.home.StartWorkoutScreen
import com.cmp.pushuptracker.ui.screen.onBoarding.OnBoardingNavigation
import com.cmp.pushuptracker.ui.screen.profileScreen.ProfileNavigation
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui.PushUpScreen
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.ui.theme.PushupTrackerTheme
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.cmp.pushuptracker.viewmodel.UtilViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PreferenceUtil.recordAppLaunch(this)
        setContent {
            val utilViewmodel = hiltViewModel<UtilViewmodel>()
            val userViewmodel = hiltViewModel<UserViewmodel>()
            val theme = utilViewmodel.theme

            var isOnboardingCompleted by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                isOnboardingCompleted = PreferenceUtil.isOnboardingCompleted(this@MainActivity)
            }
            PushupTrackerTheme(
                userSelectedTheme = theme
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(
                            bottom = innerPadding.calculateBottomPadding(),
                        )
                    ) {
                        if (isOnboardingCompleted == null) {
                            Box(modifier = Modifier.padding(innerPadding)) {
                                HomeScreenShimmer()
                            }
                        } else if (isOnboardingCompleted == true) {
                            PushUpAppNavigation(
                                utilViewmodel = utilViewmodel,
                                userViewmodel = userViewmodel,
                                analyticsLogger = analyticsLogger
                            )
                        } else if (isOnboardingCompleted != true) {
                            OnBoardingNavigation(
                                utilViewmodel = utilViewmodel,
                                userViewmodel = userViewmodel,
                                analyticsLogger = analyticsLogger
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PushUpAppNavigation(
    utilViewmodel: UtilViewmodel,
    userViewmodel: UserViewmodel,
    analyticsLogger: AnalyticsLogger,
    pushupViewModel: PushupViewModel = hiltViewModel<PushupViewModel>(),
    livePreviewViewModel: LivePreviewViewmodel = hiltViewModel<LivePreviewViewmodel>()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            analyticsLogger.logScreenView(
                screenName = analyticsNameForRoute(it),
                screenClass = it
            )
        }
    }
    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.LivePreviewScreen.route)
                CustomBottomNavBar(navController)
            else
                Box(modifier = Modifier)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(500)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController,
                    pushupViewModel,
                    userViewmodel = userViewmodel
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(navController, pushupViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileNavigation(
                    utilViewmodel,
                    navController,
                    userViewmodel,
                    analyticsLogger
                )
            }
            composable(Screen.StartWorkout.route) {
                StartWorkoutScreen(
                    navController,
                    pushupViewModel,
                    livePreviewViewModel
                )
            }
            composable(Screen.LivePreviewScreen.route) {
                PushUpScreen(
                    navController,
                    pushupViewModel,
                    livePreviewViewModel,
                    userViewmodel
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavBar(navController: NavHostController) {
    val items = remember {
        listOf(
        Screen.Home,
        Screen.History,
        Screen.Profile
        )
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth: Dp = screenWidth / items.size

    // Animated background pill under selected item
    val bgOffset by animateDpAsState(
        targetValue = itemWidth * selectedIndex,
        animationSpec = tween(300)
    )
    val bgSize by animateDpAsState(targetValue = itemWidth * 0.8f, animationSpec = tween(300))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Background pill
        Box(
            modifier = Modifier
                .offset(x = bgOffset + (itemWidth - bgSize) / 2)
                .size(width = bgSize, height = 100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, screen ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(300)
                )
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.2f else 1f,
                    animationSpec = tween(300)
                )

                Column(
                    modifier = Modifier
                        .width(itemWidth)
                        .clickable(
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.AddCircleOutline,
                        contentDescription = screen.title,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale },
                        tint = tint
                    )
                    AnimatedVisibility(visible = selected) {
                        Text(
                            text = screen.title,
                            fontSize = 12.sp,
                            color = tint,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
