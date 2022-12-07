package com.development_felber.dartapp.util.graphs.statistics.piechart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.GameUtil
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.statistics.StatisticTypeBase
import com.development_felber.dartapp.views.chart.EChartType
import com.development_felber.dartapp.views.chart.data.DataPoint
import com.development_felber.dartapp.views.chart.util.DataSet

object CheckoutDistributionStatistic : StatisticTypeBase(
    name = "Checkout Distribution",
    chartType = EChartType.PIE_CHART
){
    val DEFAULT_CHECKOUT_CATEGORIES = listOf(0, 40, 60, 100)

    override fun buildDataSet(legs: List<FinishedLeg>, filter: LegFilterBase): DataSet {
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