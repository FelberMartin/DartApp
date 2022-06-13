package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.graphs.versus.WeekdayVersusType
import com.example.dartapp.views.chart.BarChart
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.util.ColorManager

class TrainingCount() : StatisticTypeBase(
    "Training count",
    EChartType.BAR_CHART,
    TimeVersusType(), WeekdayVersusType()
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }

    override fun modifyChart(chart: Chart) {
        with(chart as BarChart) {
            xStartAtZero = false
            colorManager = ColorManager.singleColor
            showXAxisMarkers = true
        }
    }
}