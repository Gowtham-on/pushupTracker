package com.cmp.pushuptracker.ui.screen.history

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.utils.vibrate
import com.fleeys.heatmap.HeatMap
import com.fleeys.heatmap.model.Heat
import com.fleeys.heatmap.style.DaysLabelStyle
import com.fleeys.heatmap.style.HeatColor
import com.fleeys.heatmap.style.HeatFontStyle
import com.fleeys.heatmap.style.HeatMapStyle
import com.fleeys.heatmap.style.HeatStyle
import com.fleeys.heatmap.style.LabelPosition
import com.fleeys.heatmap.style.LabelStyle
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun GetHeatMap(datePushupMap: Map<String, PushUpEntity>, onHeatClick: (PushUpEntity?) -> Unit) {
    val heats = remember(datePushupMap) {
        generateHeats(datePushupMap)
    }

    val context = LocalContext.current

    HeatMap(
        data = heats,
        scrollState = rememberLazyListState(),
        style = HeatMapStyle(
            heatMapPadding = PaddingValues(10.dp, 5.dp, 10.dp, 5.dp),
            startFromEnd = true,
            labelStyle = LabelStyle(
                daysLabelStyle = DaysLabelStyle(
                    showLabel = false,
                    position = LabelPosition.START
                )
            ),
            heatStyle = HeatStyle(
                heatColor = HeatColor(
                    activeLowestColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.005f),
                    activeHighestColor = MaterialTheme.colorScheme.primary
                ),
                heatFontStyle = HeatFontStyle(
                    fontSize = 10.sp,
                    fontLightColor = MaterialTheme.colorScheme.onBackground,
                ),
                heatShape = RoundedCornerShape(3.dp),
            )
        ),
    ) {
        vibrate(context)
        onHeatClick(it.data)
    }
}


@OptIn(ExperimentalTime::class)
private fun generateHeats(datePushupMap: Map<String, PushUpEntity>): List<Heat<PushUpEntity>> {
    val localDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val startDate =
        LocalDate(localDate.year, localDate.month.number - 3, 1)
    val curDate = localDate.date


    return generateSequence(startDate) { date ->
        if (date < curDate) date + DatePeriod(days = 1) else null
    }.map { date ->
        val dateFormatted = "${date.day.toString().padStart(2, '0')}/${
            date.month.number.toString().padStart(2, '0')
        }/${date.year}"
        val entity = datePushupMap[dateFormatted]
        val value = entity?.reps?.toDouble() ?: 0.1
        Heat<PushUpEntity>(date, value, entity)
    }.toList()
}