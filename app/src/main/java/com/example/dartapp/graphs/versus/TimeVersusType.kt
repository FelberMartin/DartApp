package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.util.TimeUtil.MILLISECONDS_PER_DAY
import com.example.dartapp.views.chart.util.DataSet


class TimeVersusType(
    name: String,
    private val daysReachingBack: Int
) : VersusTypeBase(name, DataSet.Type.DATE) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        val millisReachingBack = daysReachingBack * MILLISECONDS_PER_DAY
        val oldestTimeStampIncluded = System.currentTimeMillis() - millisReachingBack
        return legs.filter { leg -> leg.endTime >= oldestTimeStampIncluded }
    }

    override fun partitionLegs(legs: List<Leg>): Map<Any, List<Leg>> {
        return legs.groupBy { leg -> leg.endTime / MILLISECONDS_PER_DAY }
    }
}