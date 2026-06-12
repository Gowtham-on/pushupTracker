package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.BuildConfig
import com.cmp.pushuptracker.camera.CameraPreview
import com.cmp.pushuptracker.camera.PushUpCounter
import com.cmp.pushuptracker.camera.processImage
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.screen.home.model.PushupQuickAdd
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentMode
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PushupUtils
import com.cmp.pushuptracker.utils.TimeUtils
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PushUpScreen(
    navController: NavHostController,
    pushupViewModel: PushupViewModel,
    livePreviewViewModel: LivePreviewViewmodel,
    userViewmodel: UserViewmodel
) {
    var reps by remember { mutableIntStateOf(0) }
    val counter = remember { PushUpCounter() }
    val selectedDayData by pushupViewModel.selectedDayData.collectAsState()
    val userData = userViewmodel.userData
    val selectedDate = remember { TimeUtils.todayStorageDate() }

    // Ensure selectedDayData tracks today's date while on this screen
    LaunchedEffect(selectedDate) {
        pushupViewModel.setSelectedDate(selectedDate)
    }

    var color by remember { mutableStateOf(Color.White) }

    LaunchedEffect(livePreviewViewModel.currentMode, livePreviewViewModel.isNoseVisibleInCamera) {
        if (livePreviewViewModel.currentMode == CurrentMode.INIT.ordinal) {
            color = if (!livePreviewViewModel.isNoseVisibleInCamera) {
                Color.Red.copy(alpha = 0.4f)
            } else {
                Color.Green.copy(alpha = 0.4f)
            }
        } else if (livePreviewViewModel.currentMode == CurrentMode.PUSHUP.ordinal) {
            color = if (!livePreviewViewModel.isNoseVisibleInCamera) {
                Color.Red.copy(alpha = 0.4f)
            } else {
                Color.Transparent
            }
        }
    }

    var workoutTimeTaken by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            workoutTimeTaken++
        }
    }

    LaunchedEffect(livePreviewViewModel.currentMode) {
        when (livePreviewViewModel.currentMode) {
            CurrentMode.PUSHUP.ordinal -> {
                isRunning = true  // start/resume ticking
            }

            CurrentMode.INTERVAL.ordinal -> {
                isRunning = false // pause ticking
            }

            CurrentMode.COMPLETED.ordinal -> {
                isRunning = false // stop ticking
                // workoutTimeTaken now has total duration
            }
        }
    }

    fun saveCurrentWorkout() {
        val totalReps = livePreviewViewModel.totalReps
        if (totalReps <= 0) return

        val completedSets = livePreviewViewModel.totalSets
        val setsToSave = if (completedSets > 0) completedSets else 1
        val addPushupData = PushupQuickAdd(
            sets = setsToSave.toString(),
            reps = totalReps.toString(),
            min = "0",
            secs = workoutTimeTaken.toString()
        )
        PushupUtils.addPushupInDb(
            selectedDayData = selectedDayData,
            selectedDate = selectedDate,
            userData = userData,
            addPushupData = addPushupData,
            userViewmodel = userViewmodel,
            pushupViewModel = pushupViewModel
        )
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                AppBar("") {
                    navController.popBackStack()
                    livePreviewViewModel.setCurrentMode(CurrentMode.INIT)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .keepScreenOn(),
                        onFrame = { imageProxy ->
                            processImage(imageProxy) { pose ->
                                if (livePreviewViewModel.currentMode == CurrentMode.PUSHUP.ordinal) {
                                    reps = counter.updatePose(pose, livePreviewViewModel, false)
                                    if (reps == livePreviewViewModel.reps) {
                                        livePreviewViewModel.incrementSets()
                                        if (livePreviewViewModel.sets == livePreviewViewModel.totalSets) {
                                            livePreviewViewModel.setCurrentMode(CurrentMode.COMPLETED)
                                        } else {
                                            livePreviewViewModel.setCurrentMode(CurrentMode.INTERVAL)
                                            counter.resetCounter()
                                            reps = 0
                                        }
                                    }
                                } else if (livePreviewViewModel.currentMode == CurrentMode.INIT.ordinal) {
                                    counter.updatePose(pose, livePreviewViewModel, true)
                                }
                            }
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .innerShadow(
                                RoundedCornerShape(5.dp),
                                shadow = Shadow(
                                    radius = 5.dp,
                                    spread = 3.dp,
                                    alpha = 1f,
                                    color = color
                                )
                            ),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (livePreviewViewModel.currentMode == CurrentMode.INIT.ordinal && !livePreviewViewModel.isNoseVisibleInCamera)
                            Text(
                                "Keep your face facing in front of the camera",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color = Color.Gray.copy(alpha = 0.7f))
                                    .padding(5.dp),
                                fontFamily = workSansFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        if (livePreviewViewModel.currentMode == CurrentMode.PUSHUP.ordinal) {
                            GetRepsCountView(livePreviewViewModel.totalReps)
                        }
                        if (BuildConfig.DEBUG) {
                            PushUpDebugOverlay(livePreviewViewModel)
                        }
                    }
                }


                if (livePreviewViewModel.currentMode != CurrentMode.COMPLETED.ordinal)
                    PushUpCountdownSection(livePreviewViewModel, navController) {
                        saveCurrentWorkout()
                        livePreviewViewModel.clearData()
                    }
                else
                    BasicAlertDialog(
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        onDismissRequest = {},
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "Good Job!!\nYou have completed your push-ups sets!! 🎉🎉",
                                fontFamily = workSansFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    saveCurrentWorkout()
                                    livePreviewViewModel.clearData()
                                    navController.popBackStack(
                                        Screen.Home.route,
                                        inclusive = false,
                                        saveState = false
                                    )
                                },
                                modifier = Modifier
                                    .height(IntrinsicSize.Min),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    "Go-Back",
                                    style = TextStyle(
                                        fontFamily = workSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun PushUpDebugOverlay(viewModel: LivePreviewViewmodel) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Phase: ${viewModel.counterPhase}",
            color = Color.White,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Text(
            text = "Arm: ${String.format(Locale.getDefault(), "%.1f", viewModel.lastArmAngle)}",
            color = Color.White,
            fontFamily = workSansFamily,
            fontSize = 12.sp
        )
        Text(
            text = "Body: ${String.format(Locale.getDefault(), "%.1f", viewModel.lastBodyLineAngle)} ${if (viewModel.isBodyLineOk) "OK" else "LOW"}",
            color = if (viewModel.isBodyLineOk) Color.White else Color.Red,
            fontFamily = workSansFamily,
            fontSize = 12.sp
        )
    }
}
