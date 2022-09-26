package com.example.dartapp.chartstuff.graphs.statistics

import com.example.dartapp.chartstuff.graphs.filter.LegFilterBase
import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.GameUtil
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

class ServeDistribution() : StatisticTypeBase(
    "Serve Distribution",
    EChartType.PIE_CHART
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        // This StatsType does not need this function
        // Consider changing the architecture to resolve this bad practice
        return 0
    }

    override fun buildDataSet(legs: List<Leg>, filter: LegFilterBase): DataSet {
        val filteredLegs = filter.filterLegs(legs)
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