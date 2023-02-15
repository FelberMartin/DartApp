package com.development_felber.dartapp.util.graphs.partitioner

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekdayLegPartitioner : LegPartitioner {


    override fun partitionLegs(sortedLegs: List<FinishedLeg>): Map<String, List<FinishedLeg>> {
        val map = emptyWeekDayListMap()

        for (leg in sortedLegs) {
            val weekDayString = Converters.toLocalDateTime(leg.endTime).format(weekdayFormatter)
            map[weekDayString]!!.add(leg)
        }

        return map
    }

    companion object {
        private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE")

        private fun emptyWeekDayListMap() : Map<String, ArrayList<FinishedLeg>> {
            val map = mutableMapOf<String, ArrayList<FinishedLeg>>()
            for (i in 0 until 7) {
                val then = LocalDateTime.now().minusDays(i.toLong())
                val weekdayKey = then.format(weekdayFormatter)
                map[weekdayKey] = ArrayList()
            }

            return map
        }
    }
}