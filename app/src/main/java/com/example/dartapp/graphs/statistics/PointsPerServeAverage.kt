package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.GamesVersusType
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.LineChart

class PointsPerServeAverage() : StatisticTypeBase(
    "Serve Avg",
    EChartType.LINE_CHART,
    GamesVersusType(), TimeVersusType()
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg -> leg.servesAvg }.average()
    }

    override fun modifyChart(chart: Chart) {
        with(chart as LineChart) {
            yStartAtZero = true
            verticalAutoPadding = false
            topAutoPadding = true
        }
    }
}