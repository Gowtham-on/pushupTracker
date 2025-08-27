package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.component.CountdownSlider
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentMode
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.ui.theme.workSansFamily
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PushUpCountdownSection(viewModel: LivePreviewViewmodel, navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        Spacer(Modifier.height(15.dp))
        if (viewModel.currentMode == CurrentMode.INTERVAL.ordinal)
            GetTimerSection(viewModel)
        else
            GetPushUpSection(viewModel, navController)

        Spacer(Modifier.height(15.dp))
    }
}

@Composable
fun GetTimerSection(viewModel: LivePreviewViewmodel) {
    var countdownSeconds by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Time Remaining",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
        )
        Text(
            "00:${String.format(Locale.getDefault(), "%02d", countdownSeconds)}",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
        )
    }
    Spacer(Modifier.height(15.dp))
    CountdownSlider(
        countdownSeconds = viewModel.interval,
        onCountDownChange = {
            countdownSeconds = it
        }
    ) {
        viewModel.setCurrentMode(CurrentMode.PUSHUP)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetPushUpSection(viewModel: LivePreviewViewmodel, navController: NavHostController) {
    var showStopSheet by remember { mutableStateOf(false) }

    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.sets != 0 && viewModel.reps != 0)
            Row(
                modifier = Modifier.weight(2f),
            ) {
                Text(
                    "Sets: ${viewModel.sets - (viewModel.totalSets % viewModel.sets)}",
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Reps: ${viewModel.reps - (viewModel.totalReps % viewModel.reps)}",
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        Button(
            onClick = {
                if (viewModel.currentMode == CurrentMode.PUSHUP.ordinal) {
                    // Handle stop action
                    showStopSheet = true
                } else {
                    // Handle start action
                    viewModel.setCurrentMode(CurrentMode.PUSHUP)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .align(Alignment.Top)
                .weight(1f)
        ) {
            Text(
                text = if (viewModel.currentMode == CurrentMode.INIT.ordinal) "Start" else "Stop",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
    if (showStopSheet)
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { state.hide() }
            },
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            val textStyle = TextStyle(
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            val buttonModifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Min)

            fun hideSheet() {
                scope.launch {
                    state.hide()
                    showStopSheet = false
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    "Are you sure you want to stop? You can quick add.",
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Cancel Button
                    Button(
                        onClick = { hideSheet() },
                        modifier = buttonModifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(16.dp)
                        ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = textStyle,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    // Stop Button
                    Button(
                        onClick = {
                            hideSheet()
                            navController.popBackStack(
                                Screen.Home.route,
                                inclusive = false,
                                saveState = false
                            )
                        },
                        modifier = buttonModifier,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Stop",
                            style = textStyle,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

}