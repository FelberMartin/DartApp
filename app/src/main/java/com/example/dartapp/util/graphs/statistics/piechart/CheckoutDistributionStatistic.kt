package com.example.dartapp.util.graphs.statistics.piechart

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.GameUtil
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.statistics.StatisticTypeBase
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

object CheckoutDistributionStatistic : StatisticTypeBase(
    name = "Checkout Distribution",
    chartType = EChartType.PIE_CHART
){
    val DEFAULT_CHECKOUT_CATEGORIES = listOf(0, 40, 60, 100)

    override fun buildDataSet(legs: List<Leg>, filter: LegFilterBase): DataSet {
        val filteredLegs = filter.filterLegs(legs)
        val checkouts = filteredLegs.map { leg -> leg.checkout }
        val partitionSizes = GameUtil.partitionSizeForLowerLimits(checkouts, DEFAULT_CHECKOUT_CATEGORIES)
        val dataPoints = partitionSizes.map { (categoryLimit, count) ->
            DataPoint(
                "$categoryLimit+",
                count
            )
        }

        val dataSet = DataSet(dataPoints)
        dataSet.dataPointXType = DataSet.Type.STRING
        return dataSet
    }
}