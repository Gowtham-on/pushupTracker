package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.component

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CountdownSlider(
    countdownSeconds: Int,
    modifier: Modifier = Modifier,
    onCountDownChange: (value: Int) -> Unit = {},
    onFinished: () -> Unit = {}
) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(key1 = countdownSeconds) {
        animatedValue.snapTo(countdownSeconds.toFloat())
        animatedValue.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = countdownSeconds * 1000,
                easing = LinearEasing
            )
        )
        onFinished()
    }

    LaunchedEffect(animatedValue) {
        snapshotFlow { animatedValue.value.toInt() }
            .distinctUntilChanged() // Prevent redundant emissions
            .collect { value ->
                if (value <= 5) {
                    val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP)
                }
                onCountDownChange(value)
            }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .background(color = Color.Gray.copy(alpha = 0.5f))
            .height(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedValue.value / countdownSeconds)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color(0xFFFF6B6B))
        )
    }
}