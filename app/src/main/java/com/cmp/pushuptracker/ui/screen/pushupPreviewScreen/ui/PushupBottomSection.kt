package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.component.CountdownSlider
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentMode
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.ui.theme.workSansFamily
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun PushUpPreviewScreen(viewModel: LivePreviewViewmodel) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        Spacer(Modifier.height(15.dp))
        if (viewModel.currentMode == CurrentMode.INTERVAL.ordinal)
            GetTimerSection(viewModel)
        else
            GetPushUpSection(viewModel)
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

@Composable
fun GetPushUpSection(viewModel: LivePreviewViewmodel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            modifier = Modifier.weight(2f),
        ) {
            Text(
                "Total Sets: ${viewModel.sets}",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                "Total Reps: ${viewModel.reps}",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                viewModel.setCurrentMode(CurrentMode.INTERVAL)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6B)
            ),
            modifier = Modifier.align(Alignment.Top).weight(1f)
        ) {
            Text(
                text = "Start",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}