package com.example.dartapp.util.graphs.statistics.piechart

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.GameUtil
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.statistics.StatisticTypeBase
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

object ServeDistributionStatistic : StatisticTypeBase(
    "Serve Distribution",
    EChartType.PIE_CHART
) {

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