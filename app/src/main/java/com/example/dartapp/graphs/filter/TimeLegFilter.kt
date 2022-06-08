package com.example.dartapp.graphs.filter

import com.example.dartapp.database.Leg
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.util.time.units.Day
import com.example.dartapp.util.time.units.Month
import com.example.dartapp.util.time.units.Quarter
import com.example.dartapp.util.time.units.Week

class TimeLegFilter() : LegFilterBase(
    filterOptions = listOf(
        FilterOption("7 Days", Pair(7, Day)),
        FilterOption("4 Weeks", Pair(4, Week)),
        FilterOption("3 Months", Pair(3, Month)),
        FilterOption("4 Quarters", Pair(4, Quarter)),
        FilterOption("All time", Pair(Int.MAX_VALUE, Day))
    )
) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        val pair = (currentFilterOption().value as Pair<Int, TimeUnit>)
        val count = pair.first
        val timeUnit = pair.second
        if (count == Int.MAX_VALUE) {
            return legs
        }
        val oldestTimeStampIncluded = timeUnit.millisAfterGoingBack(count, System.currentTimeMillis())
        return legs.filter { leg -> leg.endTime >= oldestTimeStampIncluded }
    }
}