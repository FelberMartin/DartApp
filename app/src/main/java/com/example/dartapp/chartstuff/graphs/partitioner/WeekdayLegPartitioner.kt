package com.example.dartapp.chartstuff.graphs.partitioner

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekdayLegPartitioner : LegPartitioner {


    override fun partitionLegs(sortedLegs: List<Leg>): Map<String, List<Leg>> {
        val map = emptyWeekDayListMap()

        for (leg in sortedLegs) {
            val weekDayString = Converters.toLocalDateTime(leg.endTime).format(weekdayFormatter)
            map[weekDayString]!!.add(leg)
        }

        return map
    }

    companion object {
        private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE")

        private fun emptyWeekDayListMap() : Map<String, ArrayList<Leg>> {
            val map = mutableMapOf<String, ArrayList<Leg>>()
            for (i in 0 until 7) {
                val then = LocalDateTime.now().minusDays(i.toLong())
                val weekdayKey = then.format(weekdayFormatter)
                map[weekdayKey] = ArrayList()
            }

            return map
        }
    }
}