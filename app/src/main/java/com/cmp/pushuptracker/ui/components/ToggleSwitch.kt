package com.cmp.pushuptracker.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    checkedTrackColor: Color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
    uncheckedTrackColor: Color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
    thumbColor: Color = Color.White
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                onClick = { if (enabled) onCheckedChange(!checked) }
            )
            .padding(4.dp)
    ) {
        // The switch track
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 30.dp)
                .clip(CircleShape)
                .background(if (checked) checkedTrackColor else uncheckedTrackColor),
            contentAlignment = Alignment.Center
        ) {
            // Animate thumb position
            val offset by animateDpAsState(
                targetValue = if (checked) 10.dp else (-10).dp,
                animationSpec = spring()
            )
            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
        if (label != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )
        }
    }
}