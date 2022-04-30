package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.ProgressVersusType
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.views.chart.EChartType

class ServeDistribution() : StatisticTypeBase(
    "Serve Distribution",
    EChartType.PIE_CHART,
    ProgressVersusType.defaults, TimeVersusType.defaults
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg -> leg.servesAvg }.average()
    }
}