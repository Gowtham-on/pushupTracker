package com.cmp.pushuptracker.ui.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cmp.pushuptracker.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@Composable
fun LineChartCompose(
    entry: List<Entry>,
    canShowLegend: Boolean,
    xAxisValues: List<String>,
    label: String = "Progress"
) {
    val lineColor = MaterialTheme.colorScheme.primary.toArgb()
    val lineColorAlpha = MaterialTheme.colorScheme.primary.copy(0.1f).toArgb()
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground.toArgb()

    AndroidView(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                setDrawGridBackground(false)
                description = Description().apply { text = "" }

                axisRight.isEnabled = false
                axisLeft.isEnabled = true
                axisLeft.apply {
                    textColor = onBackgroundColor
                    textSize = 12f
                    setDrawGridLines(false)
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = XAxisValueFormatter(xAxisValues)
                    textColor = onBackgroundColor
                }

                legend.apply {
                    isEnabled = canShowLegend
                    textColor = onBackgroundColor
                    textSize = 12f
                }

                // Touch interactions
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)
                marker = CustomMarkerView(context, R.layout.marker_view).apply {
                    setTextColor(onBackgroundColor)
                }
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entry, label).apply {
                color = lineColor
                setDrawCircles(true)
                circleRadius = 5f
                setCircleColor(lineColor)
                setDrawCircleHole(false)
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawValues(true)
                valueTextSize = 10f
                valueTextColor = onBackgroundColor
                setDrawFilled(true)
                fillColor = lineColorAlpha
                fillAlpha = 40
                valueFormatter = ZeroHidingValueFormatter()
            }

            chart.extraBottomOffset = 10f
            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = XAxisValueFormatter(xAxisValues)
            chart.invalidate()
            chart.animateXY(500, 1000)
        }
    )
}

class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        textView.text = "${e?.y?.toInt() ?: 0}"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }

    fun setTextColor(colorInt: Int) {
        textView.setTextColor(colorInt)
    }
}

class XAxisValueFormatter(val xAxisValues: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return xAxisValues.getOrNull(value.toInt()) ?: ""
    }
}

class ZeroHidingValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value == 0f) "" else value.toInt().toString()
    }
}
