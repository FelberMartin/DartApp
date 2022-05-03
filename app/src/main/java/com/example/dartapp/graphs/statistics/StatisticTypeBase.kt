package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.views.chart.EChartType

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    private vararg val availableVersusTypes: List<VersusTypeBase>
) {

    abstract fun reduceLegsToNumber(legs: List<Leg>) : Number

    fun getAvailableVersusTypes() : List<VersusTypeBase> {
        return availableVersusTypes.reduce { acc, list -> acc + list }
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
