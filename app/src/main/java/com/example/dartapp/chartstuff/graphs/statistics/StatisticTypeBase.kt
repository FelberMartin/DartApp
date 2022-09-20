package com.example.dartapp.chartstuff.graphs.statistics

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    private vararg val availableVersusTypes: VersusTypeBase
) {

    abstract fun reduceLegsToNumber(legs: List<Leg>) : Number

    open fun modifyChart(chart: Chart) { }

    open fun buildDataSet(legs: List<Leg>, versusType: VersusTypeBase) : DataSet {
        val filteredLegs = versusType.legFilter?.filterLegs(legs) ?: legs
        val partitioned = versusType.legPartitioner.partitionLegs(filteredLegs)
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

    fun getAvailableVersusTypes() : List<VersusTypeBase> {
        return availableVersusTypes.toList()
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

