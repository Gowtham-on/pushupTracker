package com.cmp.pushuptracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RestIntervalSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..60
) {
    // For dragging detection & thumb scaling
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragged) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)    // enough room for bubble + slider
            .padding(horizontal = 16.dp)
    ) {
        val totalWidth = maxWidth
        val span = (range.last - range.first).coerceAtLeast(1)
        // fraction along the track
        val fraction = (value - range.first).toFloat() / span.toFloat()
        // where to position the bubble
        val bubbleOffset by animateDpAsState(
            targetValue = totalWidth * fraction,
            animationSpec = tween(300)
        )

        // Bubble showing current value
        AnimatedVisibility(
            visible = isDragged,
            modifier = Modifier
                .offset(x = bubbleOffset - 24.dp, y = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${value}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // The actual slider
        Slider(
            value = value.toFloat(),
            onValueChange = {
                // round to an integer second
                onValueChange(it.roundToInt())
            },
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                )
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            interactionSource = interactionSource,
            steps = 5,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // apply scale to thumb via the default thumb slot
                .graphicsLayer { scaleX = thumbScale; scaleY = thumbScale }
        )
    }
}
