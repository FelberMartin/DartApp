package com.example.dartapp.util.graphs.statistics.linechart

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.statistics.StatisticTypeBase
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

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