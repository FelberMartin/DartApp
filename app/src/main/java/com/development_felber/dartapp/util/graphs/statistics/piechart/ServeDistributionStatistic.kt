package com.development_felber.dartapp.util.graphs.statistics.piechart

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.Leg
import com.development_felber.dartapp.util.GameUtil
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.statistics.StatisticTypeBase
import com.development_felber.dartapp.views.chart.EChartType
import com.development_felber.dartapp.views.chart.data.DataPoint
import com.development_felber.dartapp.views.chart.util.DataSet

object ServeDistributionStatistic : StatisticTypeBase(
    "Serve Distribution",
    EChartType.PIE_CHART
) {
    val DEFAULT_SERVE_CATEGORIES = listOf(0, 60, 100, 140, 180)

    override fun buildDataSet(legs: List<Leg>, filter: LegFilterBase): DataSet {
        val filteredLegs = filter.filterLegs(legs)
        val serves = filteredLegs.flatMap { leg -> Converters.toListOfInts(leg.servesList) }
        val categoryCounts = GameUtil.partitionSizeForLowerLimits(serves, DEFAULT_SERVE_CATEGORIES)

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