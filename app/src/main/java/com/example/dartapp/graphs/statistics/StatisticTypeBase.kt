package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    private vararg val availableVersusTypes: VersusTypeBase
) {

    abstract fun reduceLegsToNumber(legs: List<Leg>) : Number

    open fun modifyChart(chart: Chart) { }

    fun getAvailableVersusTypes() : List<VersusTypeBase> {
        return availableVersusTypes.toList()
    }

    companion object {
        val all = listOf(
            PointsPerServeAverage(),
            DartsPerLegAverage(),
            TrainingCount(),
            ServeDistribution()
        )
    }
}

