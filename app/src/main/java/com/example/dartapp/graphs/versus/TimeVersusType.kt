package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.util.TimeUtil.MILLISECONDS_PER_DAY
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.LineChart
import com.example.dartapp.views.chart.util.DataSet


class TimeVersusType(
    name: String,
    private val daysReachingBack: Int,
    private val daysPerGroup: Int
) : VersusTypeBase(name, DataSet.Type.DATE) {

    private val millisPerGroup = daysPerGroup * MILLISECONDS_PER_DAY

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        val millisReachingBack = daysReachingBack * MILLISECONDS_PER_DAY
        val oldestTimeStampIncluded = System.currentTimeMillis() - millisReachingBack
        return legs.filter { leg -> leg.endTime >= oldestTimeStampIncluded }
    }

    override fun partitionLegs(legs: List<Leg>): Map<Any, List<Leg>> {
        val legsByGroup = legs.groupBy { leg -> leg.endTime / millisPerGroup }
        return legsByGroup.mapKeys { it.key * millisPerGroup }
    }

    override fun modifyChart(chart: Chart) {
        if (chart is LineChart) {
            chart.xMarkers.minDistance = 180f
            chart.showVerticalGrid = false
        }
    }

    companion object {
        val defaults = listOf(
            TimeVersusType("Last week", 7, 1),
            TimeVersusType("Last month", 30, 7),
            TimeVersusType("Last quarter", 91, 30),
            TimeVersusType("Last year", 365, 91),
            TimeVersusType("All time", Int.MAX_VALUE, 100)
        )
    }
}