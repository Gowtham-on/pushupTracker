package com.cmp.pushuptracker.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.vibrate

@Composable
fun BarGraph(
    listOfCounts: List<Int>,
    barColor: Color,
    barLineThickness: Int = 1,
    barLineColor: Color? = null
) {

    val highestCount = remember(listOfCounts) { listOfCounts.maxOrNull() ?: 0 }

    val selectedIndex = remember { mutableIntStateOf(-1) }

    Box {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOfCounts.mapIndexed { index, it ->
                    GetBarItem(
                        it,
                        (it.toDouble() / highestCount.toDouble()) * 200.0,
                        barColor,
                        index,
                        selectedIndex.intValue,
                        isSelected = selectedIndex.intValue == -1 || index == selectedIndex.intValue
                    ) {
                        selectedIndex.intValue = it
                    }
                }
            }
        }
    }

}

@Composable
fun GetBarItem(
    height: Int,
    barHeight: Double,
    barColor: Color,
    index: Int,
    selectedIndex: Int,
    isSelected: Boolean,
    onClick: (index: Int) -> Unit = {}
) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var startAnimation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val animatedHeight by animateDpAsState(
        targetValue = if (startAnimation) barHeight.dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "Bar Height Animation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .height(225.dp)
                .width(40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (index == selectedIndex)
                    Text(
                        height.toString(),
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(animatedHeight)
                        .background(if (isSelected) barColor else barColor.copy(alpha = 0.25f))
                        .clickable(
                            onClick = {
                                vibrate(context, 500)
                                onClick(index)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            daysOfWeek[index],
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
fun BarGraphPreview() {
    var list = listOf(100, 120, 60, 160, 260, 30, 90)
    BarGraph(list, MaterialTheme.colorScheme.secondary)
}