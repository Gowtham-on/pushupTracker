package com.cmp.pushuptracker.ui.components// Shimmer.kt
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.lerp

private val LocalShimmerBrush = staticCompositionLocalOf<Brush> {
    error("Shimmer brush not provided")
}

@Composable
fun ShimmerContainer(
    modifier: Modifier = Modifier,
    darkBase: Color = Color(0xFF2E3240),
    darkHighlight: Color = Color(0xFF3A4356),
    lightBase: Color = Color(0xFFE9EDF4),      // soft grey for white theme
    lightHighlight: Color = Color(0xFFF7F9FF), // subtle “sheen”
    content: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val isDark = isSystemInDarkTheme()

    val base = if (isDark) darkBase else lightBase
    val highlight = if (isDark) darkHighlight else lightHighlight

    // One infinite animation that all children share
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-progress"
    )

    val brush = remember(size, progress, base, highlight) {
        val w = size.width.coerceAtLeast(1)
        val startX = progress * w
        Brush.linearGradient(
            colors = listOf(
                base,
                lerp(base, highlight, 0.85f),
                base
            ),
            start = Offset(startX, 0f),
            end = Offset(startX + w, 0f)
        )
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
    ) {
        CompositionLocalProvider(LocalShimmerBrush provides brush) {
            content()
        }
    }
}

@Composable
fun ShimmerBlock(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(12)
) {
    val brush = LocalShimmerBrush.current
    Box(modifier = modifier.clip(shape).background(brush))
}

@Composable
fun ShimmerCircle(modifier: Modifier) {
    ShimmerBlock(modifier = modifier, shape = CircleShape)
}
