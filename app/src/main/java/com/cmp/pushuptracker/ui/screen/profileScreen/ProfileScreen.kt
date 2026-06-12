package com.cmp.pushuptracker.ui.screen.profileScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cmp.pushuptracker.analytics.AnalyticsLogger
import com.cmp.pushuptracker.analytics.analyticsNameForRoute
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.screen.home.GetRedirectSection
import com.cmp.pushuptracker.ui.screen.home.GetThemeSection
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.cmp.pushuptracker.viewmodel.UtilViewmodel

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileNavigation(
    utilViewmodel: UtilViewmodel,
    homeNavigation: NavHostController,
    userViewmodel: UserViewmodel,
    analyticsLogger: AnalyticsLogger
) {

    val profileNavController = rememberNavController()
    val navBackStackEntry by profileNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            analyticsLogger.logScreenView(
                screenName = analyticsNameForRoute(it),
                screenClass = it
            )
        }
    }
    NavHost(
        contentAlignment = Alignment.TopCenter,
        navController = profileNavController,
        startDestination = Screen.Profile.route,
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
        composable(Screen.Profile.route) {
            ProfileScreen(
                homeNavigation,
                profileNavController,
                userViewmodel,
                utilViewmodel
            )
        }
        composable(Screen.ThemeChangeView.route) {
            ThemeScreen(
                profileNavController,
                utilViewmodel
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    homeNavController: NavHostController,
    profileNavController: NavHostController,
    userViewmodel: UserViewmodel,
    utilViewmodel: UtilViewmodel,
) {
    val userData = userViewmodel.userData
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {
        AppBar("Profile") {
            homeNavController.popBackStack()
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            GetStatsSection(userData)
            GetPermissionSection()
            GetReminderSection()
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            Column {
                GetThemeSection(profileNavController, utilViewmodel)
                GetRedirectSection("Privacy Policy") {
                    val url = "https://pushuptracker-c9c08.web.app/"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    homeNavController.context.startActivity(intent)
                }
                GetRedirectSection("Feature Request") {
                    val url = "https://pushuptracker.featurebase.app/dashboard/posts"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    homeNavController.context.startActivity(intent)
                }
                Spacer(Modifier.height(20.dp))
            }
        }

    }
}

@Composable
fun GetActionsCard(title: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp),
                width = 1.dp
            )
            .background(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                title,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun PreviewViews() {
    GetPermissionSection()
}
