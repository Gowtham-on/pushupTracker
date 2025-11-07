package com.cmp.pushuptracker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.surfaceColorAtElevation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmp.pushuptracker.BuildConfig
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var forceUpdatePrompt by mutableStateOf<ForceUpdatePrompt?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PreferenceUtil.recordAppLaunch(this)
        initializeRemoteConfig()
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

            val prompt = forceUpdatePrompt
            if (prompt != null) {
                val dismissHandler = if (prompt.blocking) null else {
                    { dismissOptionalUpdate() }
                }
                ForceUpdateDialog(
                    prompt = prompt,
                    onUpdateClick = { openStoreListing() },
                    onDismiss = dismissHandler
                )
            }
        }
    }

    private fun initializeRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else {
                TimeUnit.HOURS.toSeconds(REMOTE_CONFIG_FETCH_INTERVAL_HOURS)
            }
        }
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(remoteConfigDefaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Remote Config fetch failed", task.exception)
                }
                evaluateForceUpdateRequirement()
            }.addOnFailureListener {
                Log.e(TAG, "Failed to fetch remote config", it)
            }
    }

    private fun evaluateForceUpdateRequirement() {
        val minSupportedVersion = remoteConfig.getLong(KEY_MIN_SUPPORTED_VERSION_CODE).toInt()
        Log.d("MainActivity", "Min supported version: $minSupportedVersion")
        if (BuildConfig.VERSION_CODE >= minSupportedVersion) {
            forceUpdatePrompt = null
            return
        }
        val blocking = remoteConfig.getBoolean(KEY_FORCE_UPDATE_BLOCKING)
        val prompt = ForceUpdatePrompt(
            title = remoteConfig.getString(KEY_FORCE_UPDATE_TITLE).ifBlank { DEFAULT_FORCE_UPDATE_TITLE },
            message = remoteConfig.getString(KEY_FORCE_UPDATE_MESSAGE).ifBlank { DEFAULT_FORCE_UPDATE_MESSAGE },
            ctaLabel = remoteConfig.getString(KEY_FORCE_UPDATE_CTA).ifBlank { DEFAULT_FORCE_UPDATE_CTA },
            blocking = blocking
        )
        forceUpdatePrompt = prompt
    }

    private fun openStoreListing() {
        val packageName = packageName
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        )
        try {
            startActivity(playStoreIntent)
        } catch (activityNotFound: ActivityNotFoundException) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            )
            try {
                startActivity(webIntent)
            } catch (error: Exception) {
                Toast.makeText(this, "Unable to open Play Store listing", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Unable to open Play Store listing", error)
            }
        }
    }

    private fun dismissOptionalUpdate() {
        forceUpdatePrompt = null
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_MIN_SUPPORTED_VERSION_CODE = "min_supported_version_code"
        private const val KEY_FORCE_UPDATE_TITLE = "force_update_title"
        private const val KEY_FORCE_UPDATE_MESSAGE = "force_update_message"
        private const val KEY_FORCE_UPDATE_CTA = "force_update_cta"
        private const val KEY_FORCE_UPDATE_BLOCKING = "force_update_blocking"
        private const val DEFAULT_FORCE_UPDATE_TITLE = "A shiny new Pushup Tracker is here!"
        private const val DEFAULT_FORCE_UPDATE_MESSAGE =
            "We polished a few things to make your workouts smoother. Please grab the latest version to continue."
        private const val DEFAULT_FORCE_UPDATE_CTA = "Update now"
        private const val REMOTE_CONFIG_FETCH_INTERVAL_HOURS = 6L
        private val remoteConfigDefaults = mapOf(
            KEY_MIN_SUPPORTED_VERSION_CODE to BuildConfig.VERSION_CODE,
            KEY_FORCE_UPDATE_BLOCKING to true,
            KEY_FORCE_UPDATE_TITLE to DEFAULT_FORCE_UPDATE_TITLE,
            KEY_FORCE_UPDATE_MESSAGE to DEFAULT_FORCE_UPDATE_MESSAGE,
            KEY_FORCE_UPDATE_CTA to DEFAULT_FORCE_UPDATE_CTA
        )
    }
}

private const val OPTIONAL_DISMISS_LABEL = "Later"

@Composable
private fun ForceUpdateDialog(
    prompt: ForceUpdatePrompt,
    onUpdateClick: () -> Unit,
    onDismiss: (() -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = colorScheme.surfaceColorAtElevation(6.dp)
    AlertDialog(
        onDismissRequest = {
            if (!prompt.blocking) {
                onDismiss?.invoke()
            }
        },
        title = {
            Text(
                text = prompt.title,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = prompt.message,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onUpdateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text(prompt.ctaLabel)
            }
        },
        dismissButton = if (!prompt.blocking && onDismiss != null) {
            {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorScheme.primary
                    )
                ) {
                    Text(OPTIONAL_DISMISS_LABEL)
                }
            }
        } else {
            null
        },
        containerColor = containerColor,
        titleContentColor = colorScheme.onSurface,
        textContentColor = colorScheme.onSurfaceVariant,
        iconContentColor = colorScheme.primary,
        properties = DialogProperties(
            dismissOnBackPress = !prompt.blocking,
            dismissOnClickOutside = !prompt.blocking
        )
    )
}

private data class ForceUpdatePrompt(
    val title: String,
    val message: String,
    val ctaLabel: String,
    val blocking: Boolean
)

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
