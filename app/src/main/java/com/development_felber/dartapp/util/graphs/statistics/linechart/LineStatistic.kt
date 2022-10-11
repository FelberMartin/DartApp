package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.Leg
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.statistics.StatisticTypeBase
import com.development_felber.dartapp.views.chart.EChartType
import com.development_felber.dartapp.views.chart.data.DataPoint
import com.development_felber.dartapp.views.chart.util.DataSet

abstract class LineStatistic(
    name: String
) : StatisticTypeBase(
    name = name,
    chartType = EChartType.LINE_CHART
) {

    abstract fun reduceLegsToNumber(legs: List<Leg>) : Number

    override fun buildDataSet(legs: List<Leg>, filter: LegFilterBase) : DataSet {
        val filteredLegs = filter.filterLegs(legs)
        val partitioner = statisticSpecificPartitioner ?: filter.partitioner
        val partitioned = partitioner.partitionLegs(filteredLegs)
        val dataPoints = partitioned.map { partition ->
            DataPoint(
                partition.key,
                reduceLegsToNumber(partition.value)
            )
        }

        val dataSet = DataSet(dataPoints)
        dataSet.dataPointXType = DataSet.Type.STRING
        return dataSet
    }

}