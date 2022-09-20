package com.example.dartapp.chartstuff.graphs.filter

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.graphs.filter.FilterOption
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.util.time.units.Day
import com.example.dartapp.util.time.units.Month
import com.example.dartapp.util.time.units.Quarter
import com.example.dartapp.util.time.units.Week
import java.time.LocalDateTime

class TimeLegFilter() : LegFilterBase(
    filterOptions = listOf(
        FilterOption("7 Days", Pair(7, Day)),
        FilterOption("4 Weeks", Pair(4, Week)),
        FilterOption("3 Months", Pair(3, Month)),
        FilterOption("4 Quarters", Pair(4, Quarter)),
        FilterOption("All time", Pair(ALL_TIME_COUNT, Day))
    )
) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        val pair = (currentFilterOption().value as Pair<Int, TimeUnit>)
        val count = pair.first
        val timeUnit = pair.second
        if (count == Int.MAX_VALUE) {
            return legs
        }
        val youngestIncluded = timeUnit.afterGoingBack(count - 1, LocalDateTime.now())
        return legs.filter { leg -> Converters.toLocalDateTime(leg.endTime).isAfter(youngestIncluded) }
    }

    companion object {
        const val ALL_TIME_COUNT = Int.MAX_VALUE
    }
}