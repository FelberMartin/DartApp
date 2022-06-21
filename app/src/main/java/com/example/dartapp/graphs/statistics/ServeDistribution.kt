package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.GamesVersusType
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.util.GameUtil
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

class ServeDistribution() : StatisticTypeBase(
    "Serve Distribution",
    EChartType.PIE_CHART,
    GamesVersusType(), TimeVersusType()
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        // This StatsType does not need this function
        // Consider changing the architecture to resolve this bad practice
        return 0
    }

    override fun buildDataSet(legs: List<Leg>, versusType: VersusTypeBase): DataSet {
        val filteredLegs = versusType.legFilter?.filterLegs(legs) ?: legs
        val serves = filteredLegs.flatMap { leg -> Converters.toListOfInts(leg.servesList) }
        val categoryCounts = GameUtil.countServesForCategories(serves)

        val dataPoints = categoryCounts.map { (categoryLimit, count) ->
            DataPoint(
                GameUtil.nameServeCategory(categoryLimit),
                count
            )
        }

        val dataSet = DataSet(dataPoints)
        dataSet.dataPointXType = DataSet.Type.STRING
        return dataSet
    }
}