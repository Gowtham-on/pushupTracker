package com.cmp.pushuptracker.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedToggleTabSwitcher(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    tabWidth: Dp = 100.dp,
    modifier: Modifier = Modifier,
    tabs: List<String> = listOf("Week", "Month")
) {
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
    val tabCount = tabs.size

    val targetWidth = tabWidth / tabCount
    val indicatorOffset by animateDpAsState(
        targetValue = targetWidth * selectedIndex,
        label = "TabIndicatorOffset"
    )

    val selectedColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .height(30.dp)
            .width(tabWidth)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
    ) {
        // Animated background indicator
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f / tabCount)
                .offset(x = indicatorOffset)
                .padding(2.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.background)
        )

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Text(
                    text = tab,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            onClick = {
                                onTabSelected(tab)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ),
                    color = if (isSelected) selectedColor else textColor.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper function to get screen width in dp
@Composable
private fun screenWidthDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}
