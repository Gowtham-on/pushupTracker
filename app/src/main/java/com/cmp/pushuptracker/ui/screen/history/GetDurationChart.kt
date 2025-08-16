package com.cmp.pushuptracker.ui.screen.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.LineChartCompose
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.TimeUtils
import com.github.mikephil.charting.data.Entry

@Composable
fun GetDurationChart(datePushupMap: Map<String, PushUpEntity>) {
    var weekIndex by remember { mutableIntStateOf(0) }
    val weekDates = TimeUtils.getWeekDates(weekIndex)
    val listOfXAxis = remember(weekIndex) { weekDates.map { it.substring(0, 5) } }
    val timeEntry = remember(weekIndex, datePushupMap) {
        weekDates.mapIndexed { index, date ->
            val duration = datePushupMap[date]?.duration?.toFloat() ?: 0f
            val durationInMinutes = duration.toFloat() / 60f
            Entry(index.toFloat(), durationInMinutes)
        }
    }
    val weekTitle = remember(weekIndex) { TimeUtils.getWeekDateRangeString(weekIndex) }

    Text(
        "Workout Duration",
        modifier = Modifier.padding(horizontal = 16.dp),
        fontFamily = workSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .clickable { weekIndex-- }
                    .size(30.dp)
            )

            Text(
                weekTitle,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        onClick = { weekIndex++ },
                        enabled = weekIndex < 0
                    )
                    .size(30.dp),
                tint = if (weekIndex < 0) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.5f
                )
            )
        }
        LineChartCompose(
            timeEntry,
            canShowLegend = false,
            xAxisValues = listOfXAxis,
            allowFloat = true
        )
    }

}