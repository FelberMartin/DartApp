package com.example.dartapp.ui.screens.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.dartapp.ui.shared.Background
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlin.random.Random

@Composable
fun Chart() {
    Background {
        Column() {
            LineChart()
            BarChart()
        }
    }
}

@Composable
private fun LineChart() {
    AndroidView(
        modifier = Modifier.size(300.dp),
        factory = { context ->
            LineChart(context)
        },
        update = { chart ->
            val entries = ArrayList<Entry>()
            val random = Random(1L)
            repeat(8) {
                entries.add(Entry(it.toFloat(), (random.nextInt(-10, 20)).toFloat()))
            }
            val dataSet = LineDataSet(entries, "Label")
            dataSet.lineWidth = 3f
            val color1 = Color(0xFF673AB7).toArgb()
            dataSet.color = color1
            dataSet.setCircleColor(color1)
            dataSet.circleHoleColor = color1
            dataSet.setDrawValues(false)
            dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            val lineData = LineData(dataSet)
            chart.xAxis.setDrawGridLines(false)
            chart.axisRight.isEnabled = false
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
//            chart.animateX(500, Easing.EaseInOutExpo)
            chart.data = lineData
        }
    )
}

@Composable
private fun BarChart() {
    AndroidView(
        modifier = Modifier.size(300.dp),
        factory = { context ->
            BarChart(context)
        },
        update = { chart ->
            val entries = ArrayList<BarEntry>()
            val random = Random(1L)
            repeat(8) {
                entries.add(BarEntry(it.toFloat(), (random.nextInt(0, 20)).toFloat()))
            }
            val dataSet = BarDataSet(entries, "Label")
            val color1 = Color(0xFF673AB7).toArgb()
            dataSet.color = color1
            dataSet.setDrawValues(false)
            val barData = BarData(dataSet)
            chart.xAxis.setDrawGridLines(false)
            chart.axisRight.isEnabled = false
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.legend.isEnabled = false
            chart.description.isEnabled = false
            chart.animateY(500, Easing.EaseInOutExpo)
            chart.isHighlightFullBarEnabled
            chart.data = barData
        }
    )
}


@Preview(widthDp = 300, heightDp = 600)
@Composable
fun previewChart() {
    Chart()
}