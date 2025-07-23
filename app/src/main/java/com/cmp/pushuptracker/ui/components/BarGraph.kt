package com.cmp.pushuptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.utils.vibrate

private val DAYS_OF_WEEK = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun BarGraph(
    counts: List<Int>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    // only recompute max when the list contents actually change
    val maxCount = remember(counts) { counts.maxOrNull() ?: 1 }
    var selectedIndex by remember { mutableStateOf(-1) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        counts.forEachIndexed { index, count ->
            key(index) {
                BarGraphItem(
                    count = count,
                    maxCount = maxCount,
                    barColor = barColor,
                    isSelected = selectedIndex != -1 && selectedIndex == index,
                    selectedIndex = selectedIndex,
                    onClick = { selectedIndex = index },
                    dayLabel = DAYS_OF_WEEK[index]
                )
            }
        }
    }
}

@Composable
fun BarGraphItem(
    count: Int,
    maxCount: Int,
    barColor: Color,
    isSelected: Boolean,
    selectedIndex: Int,
    onClick: () -> Unit,
    dayLabel: String
) {
    val context = LocalContext.current

    // one shared source per item
    val interactionSource = remember { MutableInteractionSource() }

    // direct Dp calculation
    val targetDp = (count / maxCount.toFloat() * 200.dp.value).dp


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
                vibrate(context, 50)
            }
            .width(40.dp)
    ) {
        // show the number only if selected
        Text(
            text = if (isSelected) count.toString() else "",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(targetDp)
                .fillMaxWidth()
                .background(
                    color = if (isSelected || selectedIndex == -1) barColor else barColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = dayLabel,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
