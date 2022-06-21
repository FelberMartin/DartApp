package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.filter.LegFilterBase
import com.example.dartapp.graphs.partitioner.LegPartitioner
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

abstract class VersusTypeBase (
    val name: String,
    val legFilter: LegFilterBase?,
    val legPartitioner: LegPartitioner
){

    fun buildDataSet(legs: List<Leg>, reducer: (List<Leg>) -> Number) : DataSet {
        val filteredLegs = legFilter?.filterLegs(legs) ?: legs
        val partitioned = legPartitioner.partitionLegs(filteredLegs)
        val dataPoints = partitioned.map { partition ->
            DataPoint(
                partition.key,
                reducer.invoke(partition.value)
            )
        }

        val dataSet = DataSet(dataPoints)
        dataSet.dataPointXType = DataSet.Type.STRING
        return dataSet
    }

    open fun filterSeekBarIndexChanged(index: Int) {
        if (legFilter?.filterOptions?.size ?: 0 <= index) {
            return
        }
        legFilter?.filterOptionIndex = index
    }

    open fun modifyChart(chart: Chart) {}

}