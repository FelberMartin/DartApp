package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.util.TimeUtil.MILLISECONDS_PER_DAY
import com.example.dartapp.util.weekDayString
import com.example.dartapp.views.chart.util.DataSet
import java.util.*

class WeekdayVersusType() : VersusTypeBase("Weekdays", DataSet.Type.STRING) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        return legs
    }

    override fun partitionLegs(legs: List<Leg>): Map<Any, List<Leg>> {
        val map = emptyWeekDayListMap()

        for (leg in legs) {
            val weekDayString = Date(leg.endTime).weekDayString()
            map[weekDayString]!!.add(leg)
        }

        return map
    }

    companion object {
        val defaults = listOf(
            WeekdayVersusType()
        )

        private fun emptyWeekDayListMap() : Map<Any, ArrayList<Leg>> {
            val map = HashMap<Any, ArrayList<Leg>>()
            val now = System.currentTimeMillis()
            for (i in 0 until 7) {
                val millisOffset = i * MILLISECONDS_PER_DAY
                val then = Date(now - millisOffset)
                map[then.weekDayString()] = ArrayList()
            }

            return map
        }
    }
}