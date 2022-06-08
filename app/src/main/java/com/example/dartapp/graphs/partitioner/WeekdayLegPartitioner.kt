package com.example.dartapp.graphs.partitioner

import com.example.dartapp.database.Leg
import com.example.dartapp.util.time.TimeUtil
import com.example.dartapp.util.weekDayString
import java.util.*

class WeekdayLegPartitioner : LegPartitioner {

    override fun partitionLegs(sortedLegs: List<Leg>): Map<Any, List<Leg>> {
        val map = WeekdayLegPartitioner.emptyWeekDayListMap()

        for (leg in sortedLegs) {
            val weekDayString = Date(leg.endTime).weekDayString()
            map[weekDayString]!!.add(leg)
        }

        return map
    }

    companion object {
        private fun emptyWeekDayListMap() : Map<Any, ArrayList<Leg>> {
            val map = HashMap<Any, ArrayList<Leg>>()
            val now = System.currentTimeMillis()
            for (i in 0 until 7) {
                val millisOffset = i * TimeUtil.MILLISECONDS_PER_DAY
                val then = Date(now - millisOffset)
                map[then.weekDayString()] = ArrayList()
            }

            return map
        }
    }
}