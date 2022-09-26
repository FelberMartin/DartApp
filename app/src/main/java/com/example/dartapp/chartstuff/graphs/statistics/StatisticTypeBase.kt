package com.example.dartapp.chartstuff.graphs.statistics

import com.example.dartapp.chartstuff.graphs.filter.LegFilterBase
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    val availableFilterCategories: List<LegFilterBase.Category> = listOf(LegFilterBase.Category.ByGameCount,
        LegFilterBase.Category.ByTime)
) {

    abstract fun reduceLegsToNumber(legs: List<Leg>) : Number

    open fun modifyChart(chart: Chart) { }

    open fun buildDataSet(legs: List<Leg>, filter: LegFilterBase) : DataSet {
        val filteredLegs = filter.filterLegs(legs)
        val partitioned = filter.partitioner.partitionLegs(filteredLegs)
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

    override fun equals(other: Any?): Boolean {
        if (other !is StatisticTypeBase) {
            return false
        }
        return this.name == other.name
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

