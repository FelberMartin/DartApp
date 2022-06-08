package com.example.dartapp.graphs.partitioner

import com.example.dartapp.database.Leg
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.util.time.units.Day


class TimeLegPartitioner() : LegPartitioner {

    var timeUnit: TimeUnit = Day

    override fun partitionLegs(sortedLegs: List<Leg>): Map<Any, List<Leg>> {
        val legsByStartOfUnit = mutableMapOf<Long, ArrayList<Leg>>()
        for (leg in sortedLegs) {
            val startOfUnit = timeUnit.toStartOfUnit(leg.endTime)
            if (!legsByStartOfUnit.containsKey(startOfUnit)) {
                legsByStartOfUnit[startOfUnit] = ArrayList()
            }
            legsByStartOfUnit[startOfUnit]!!.add(leg)
        }

        return legsByStartOfUnit.mapKeys { timeUnit.toUiString(it.key) }
    }
}