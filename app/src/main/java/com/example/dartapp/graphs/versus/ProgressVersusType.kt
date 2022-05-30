package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.LineChart
import com.example.dartapp.views.chart.util.DataSet

class ProgressVersusType(
    private val backCountLimit: Int
) : VersusTypeBase(makeName(backCountLimit), DataSet.Type.STRING) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        var start = legs.size - backCountLimit
        if (start < 0) {
            start = 0
        }
        return legs.subList(start, legs.size)
    }

    override fun partitionLegs(legs: List<Leg>): Map<Any, List<Leg>> {
        val map = mutableMapOf<Any, List<Leg>>()
        val partitionSize = calculatePartitionSize(legs)
        var index = 0
        while (index < legs.size) {
            var toIndex = index + partitionSize
            if (toIndex > legs.size) {
                toIndex = legs.size
            }
            val partitionName = getPartitionName(index, toIndex - 1)
            map[partitionName] = legs.subList(index, toIndex)
            index = toIndex
        }
        return map
    }

    private fun getPartitionName(fromIndex: Int, toIndex: Int): String {
        if (fromIndex == toIndex) {
            return "$fromIndex"
        }

        return "$fromIndex-$toIndex"
    }

    private fun calculatePartitionSize(legs: List<Leg>): Int {
        val partitionCount = 10
        var partitionSize = legs.size / partitionCount
        if (partitionSize <= 0) {
            partitionSize = 1
        }
        return partitionSize
    }

    override fun modifyChart(chart: Chart) {
        if (chart is LineChart) {
            chart.showXAxisMarkers = false
            chart.showVerticalGrid = false
        }
    }

    companion object {
        val defaults = listOf(
            ProgressVersusType(10),
            ProgressVersusType(100),
            ProgressVersusType(Int.MAX_VALUE)
        )

        fun makeName(limit: Int) : String {
            if (limit == Int.MAX_VALUE) {
                return "All legs"
            }
            return "Last $limit legs"
        }
    }
}

