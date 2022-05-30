package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

abstract class VersusTypeBase (
    val name: String,
    val dataSetType: DataSet.Type
){

    fun buildDataSet(legs: List<Leg>, reducer: (List<Leg>) -> Number) : DataSet {
        val filteredLegs = filterLegs(legs)
        val partitioned = partitionLegs(filteredLegs)
        val dataPoints = partitioned.map { partition ->
            DataPoint(
                partition.key,
                reducer.invoke(partition.value)
            )
        }

        val dataSet = DataSet(dataPoints)
        dataSet.dataPointXType = dataSetType
        return dataSet
    }

    protected open fun filterLegs(legs: List<Leg>) : List<Leg> {
        return legs
    }

    abstract fun partitionLegs(legs: List<Leg>) : Map<Any, List<Leg>>


}