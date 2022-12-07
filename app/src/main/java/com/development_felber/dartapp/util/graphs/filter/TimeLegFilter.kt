package com.development_felber.dartapp.util.graphs.filter

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.graphs.partitioner.TimeLegPartitioner
import com.development_felber.dartapp.util.time.TimeUnit
import com.development_felber.dartapp.util.time.units.Day
import com.development_felber.dartapp.util.time.units.Month
import com.development_felber.dartapp.util.time.units.Quarter
import com.development_felber.dartapp.util.time.units.Week
import java.time.LocalDateTime

class TimeLegFilter private constructor(
    name: String,
    val timeUnitCount: Int,
    val timeUnit: TimeUnit
) : LegFilterBase(
    name = name,
    partitioner = TimeLegPartitioner(timeUnit, timeUnitCount)
) {

    override fun filterLegs(legs: List<FinishedLeg>): List<FinishedLeg> {
        if (timeUnitCount == Int.MAX_VALUE) {
            return legs
        }
        val youngestIncluded = timeUnit.afterGoingBack(timeUnitCount - 1, LocalDateTime.now())
        return legs.filter { leg -> Converters.toLocalDateTime(leg.endTime).isAfter(youngestIncluded) }
    }

    companion object {
        const val ALL_TIME_COUNT = Int.MAX_VALUE

        val days = TimeLegFilter("7 Days", 7, Day)
        val weeks = TimeLegFilter("4 Weeks", 4, Week)
        val months = TimeLegFilter("3 Months", 3, Month)
        val quarters = TimeLegFilter("4 Quarters", 4, Quarter)
        val allTime = TimeLegFilter("All time", ALL_TIME_COUNT, Day)

        val allFilters = listOf(days, weeks, months, quarters, allTime)
    }
}