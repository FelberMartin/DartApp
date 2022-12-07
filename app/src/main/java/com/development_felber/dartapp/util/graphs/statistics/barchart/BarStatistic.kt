package com.development_felber.dartapp.util.graphs.statistics.barchart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.partitioner.LegPartitioner
import com.development_felber.dartapp.util.graphs.statistics.StatisticTypeBase
import com.development_felber.dartapp.views.chart.BarChart
import com.development_felber.dartapp.views.chart.Chart
import com.development_felber.dartapp.views.chart.EChartType
import com.development_felber.dartapp.views.chart.data.DataPoint
import com.development_felber.dartapp.views.chart.util.DataSet

abstract class BarStatistic(
    name: String,
    statisticSpecificPartitioner: LegPartitioner? = null
) : StatisticTypeBase(
    name = name,
    chartType = EChartType.BAR_CHART,
    availableFilterCategories = listOf(LegFilterBase.Category.ByTime),
    statisticSpecificPartitioner = statisticSpecificPartitioner
){

    abstract fun reduceLegsToNumber(legs: List<FinishedLeg>) : Number

    override fun buildDataSet(legs: List<FinishedLeg>, filter: LegFilterBase) : DataSet {
        val filteredLegs = filter.filterLegs(legs)
        val partitioner = statisticSpecificPartitioner ?: filter.partitioner
        val partitioned = partitioner.partitionLegs(filteredLegs)
        val dataPoints = partitioned.map { partition ->
            DataPoint(
                partition.key,
                reduceLegsToNumber(partition.value)
            )
        }

        val dataSet = DataSet(sortDataPoints(dataPoints))
        dataSet.dataPointXType = DataSet.Type.STRING
        return dataSet
    }

    open fun sortDataPoints(dataPoints: List<DataPoint>) : List<DataPoint> {
        return dataPoints
    }

    override fun modifyChart(chart: Chart) {
        with(chart as BarChart) {
            xStartAtZero = false
            colorManager.onlyUseOneGraphColor = true
            showXAxisMarkers = true
            verticalAutoPadding = false
            topAutoPadding = true
        }
    }

}