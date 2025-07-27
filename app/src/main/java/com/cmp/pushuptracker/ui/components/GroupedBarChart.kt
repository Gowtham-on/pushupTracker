package com.cmp.pushuptracker.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter


val color1 = androidx.compose.ui.graphics.Color(0xFF3F51B5) // Indigo
val color2 = androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
val color3 = androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange

@Composable
fun GroupedBarChartCompose(pushupData: List<PushUpEntity>) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()
    val thirdColor = color2.toArgb()
    val onBackground = MaterialTheme.colorScheme.onBackground.toArgb()
    val background = MaterialTheme.colorScheme.background.toArgb()

    val repsEntry = pushupData.mapIndexed { index, it ->
        BarEntry(index.toFloat(), it.reps.toFloat())
    }

    val setsEntry = pushupData.mapIndexed { index, it ->
        BarEntry(index.toFloat(), it.sets.toFloat())
    }

    val timeEntry = pushupData.mapIndexed { index, it ->
        BarEntry(index.toFloat(), it.duration.toFloat())
    }


    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                setDrawGridBackground(false)
                description = Description().apply { text = "" }

                axisRight.apply {
                    setDrawGridLines(false)
                    textColor = background
                    textSize = 0f
                    setDrawAxisLine(false)
                }

                xAxis.apply {
                    textColor = background
                    textSize = 0f
                    setDrawAxisLine(false)
                    setDrawGridLines(false)
                }

                axisLeft.apply {
                    textColor = background
                    textSize = 0f
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                }

                legend.apply {
                    formSize = 10f
                    textSize = 12f
                    textColor = onBackground
                    xEntrySpace = 15f
                }
            }
        },
        update = { chart ->
            val groupSpace = 0.19f
            val barSpace = 0.01f
            val barWidth = 0.1f

            val set1 = BarDataSet(repsEntry, "Total Reps").apply {
                valueFormatter = IntegerValueFormatter()
                color = primaryColor
                valueTextColor = onBackground
                valueTextSize = 9f
            }

            val set2 = BarDataSet(setsEntry, "Total Sets").apply {
                valueFormatter = IntegerValueFormatter()
                color = secondaryColor
                valueTextColor = onBackground
                valueTextSize = 9f
            }

            val set3 = BarDataSet(timeEntry, "Total Workout Time (sec)").apply {
                valueFormatter = IntegerValueFormatter()
                color = thirdColor
                valueTextColor = onBackground
                valueTextSize = 9f
            }

            val data = BarData(set1, set2, set3)
            chart.data = data
            data.barWidth = barWidth

            chart.extraBottomOffset = 0f
            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum =
                0f + chart.barData.getGroupWidth(groupSpace, barSpace) * repsEntry.size

            chart.groupBars(0f, groupSpace, barSpace)
            chart.invalidate()
        }
    )
}

class IntegerValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
    }
}