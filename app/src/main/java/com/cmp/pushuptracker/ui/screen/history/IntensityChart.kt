package com.cmp.pushuptracker.ui.screen.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.LineChartCompose
import com.cmp.pushuptracker.utils.TimeUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.EntryXComparator
import kotlin.math.max

// -------------------------------------------------------------
// 1) IntensityChart: reps per minute = reps / (durationSec / 60f)
// -------------------------------------------------------------
@Composable
fun IntensityChart(
    modifier: Modifier = Modifier,
    datePushupMap: Map<String, PushUpEntity>,
    weekIndex: Int,
    // optional goal line, set null to hide
    intensityGoal: Float? = null
) {
    // Build weekly x-axis labels and y values
    val weekDates = remember(weekIndex) { TimeUtils.getWeekDates(weekIndex) }
    val xLabels = remember(weekIndex) { weekDates.map { it.take(5) } } // "MM-dd" -> "MM-dd".take(5)

    val entries = remember(weekIndex, datePushupMap) {
        weekDates.mapIndexed { idx, date ->
            val item = datePushupMap[date]
            val reps = item?.reps ?: 0
            val durationSec = max(item?.duration ?: 0, 0)
            val rpm = if (durationSec > 0) reps / (durationSec / 60f) else 0f
            Entry(idx.toFloat(), rpm)
        }.sortedWith(EntryXComparator())
    }

    LineChartCompose(entries, false, xLabels, "Reps per minute", false)
}

// -------------------------------------------------------------------
// 2) AvgRepsPerSetChart: avg = reps / sets (bar chart, per-day)
// -------------------------------------------------------------------
@Composable
fun AvgRepsPerSetChart(
    modifier: Modifier = Modifier,
    datePushupMap: Map<String, PushUpEntity>,
    weekIndex: Int
) {
    val weekDates = remember(weekIndex) { TimeUtils.getWeekDates(weekIndex) }
    val xLabels = remember(weekIndex) { weekDates.map { it.take(5) } }

    val entries = remember(weekIndex, datePushupMap) {
        weekDates.mapIndexed { idx, date ->
            val item = datePushupMap[date]
            val reps = item?.reps ?: 0
            val sets = item?.sets ?: 0
            val avg = if (sets > 0) reps.toFloat() / sets.toFloat() else 0f
            BarEntry(idx.toFloat(), avg)
        }
    }

    AndroidView(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    valueFormatter = IndexAxisValueFormatter(xLabels)
                    labelRotationAngle = -45f
                }

                axisLeft.apply {
                    axisMinimum = 0f
                    setDrawGridLines(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            // show whole numbers when close to int
                            return if (value % 1f == 0f) value.toInt()
                                .toString() else String.format("%.1f", value)
                        }
                    }
                }
                axisRight.isEnabled = false

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                animateY(600)
            }
        },
        update = { chart ->
            val set = BarDataSet(entries, "Avg reps / set").apply {
                valueTextSize = 10f
                setDrawValues(true)
            }
            chart.data = BarData(set).apply { barWidth = 0.6f }
            chart.invalidate()
        }
    )
}
