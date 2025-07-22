package com.cmp.pushuptracker.ui.screen.ProfileScreen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.viewmodel.UtilViewmodel
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileNavigation(utilViewmodel: UtilViewmodel, homeNavigation: NavHostController) {
    val profileNavController = rememberNavController()
    AnimatedNavHost(
        navController = profileNavController,
        startDestination = Screen.Profile.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
    ) {
        composable(
            Screen.Profile.route,
        ) {
            ProfileScreen(
                homeNavigation,
                utilViewmodel,
                profileNavController
            )
        }
        composable(
            Screen.ThemeChangeView.route,
        ) {
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
    utilViewmodel: UtilViewmodel,
    profileNavController: NavHostController
) {
    Scaffold {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background
                )
        ) {
            AppBar("Profile") {
                homeNavController.popBackStack()
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                GetProfileSection()
                Spacer(Modifier.height(20.dp))
                GetStatsSection()
                Spacer(Modifier.height(20.dp))
                GetActionsSection(profileNavController)
            }

        }
    }
}


@Composable
fun GetProfileSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(125.dp)
                .background(color = MaterialTheme.colorScheme.secondary)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Ethan Carter",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun GetStatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        GetStatsCard("12,345", "Total\nReps", Modifier.weight(1f))
        GetStatsCard("32", "Current\nStreak", Modifier.weight(1f))
        GetStatsCard("150", "Personal\nBest", Modifier.weight(1f))
    }
}

@Composable
fun GetStatsCard(count: String, title: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .border(
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp),
                width = 1.dp
            )
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(Modifier.height(10.dp))
            Text(
                title,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun GetActionsSection(navController: NavHostController) {
    Column {
        Text(
            "Actions",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))
        Row {
            GetActionsCard("Edit Profile", R.drawable.edit_image, Modifier.weight(1f)) { }
            Spacer(Modifier.width(15.dp))
            GetActionsCard("Notifications", R.drawable.notification_image, Modifier.weight(1f)) { }
        }
        Spacer(Modifier.height(16.dp))
        Row {
            GetActionsCard("Themes", R.drawable.theme_image, Modifier.weight(1f)) {
                navController.navigate(Screen.ThemeChangeView.route)
            }
            Spacer(Modifier.width(15.dp))
            GetActionsCard("Features", R.drawable.q_a_image, Modifier.weight(1f)) { }
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
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}