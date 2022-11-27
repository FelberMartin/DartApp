package com.development_felber.dartapp.util.graphs.partitioner

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.util.graphs.filter.TimeLegFilter
import com.development_felber.dartapp.util.time.TimeUnit
import com.development_felber.dartapp.util.time.units.Day
import com.development_felber.dartapp.util.time.units.Quarter
import java.time.LocalDateTime


class TimeLegPartitioner(
    var timeUnit: TimeUnit = Day,
    var timeUnitCount: Int = 1
) : LegPartitioner {


    override fun partitionLegs(sortedLegs: List<Leg>): Map<String, List<Leg>> {
        if (timeUnitCount == TimeLegFilter.ALL_TIME_COUNT) {
            handleAllTimeOption(sortedLegs)
        }
        val oneForEachPartition = getOneForEachPartition()
        val legsByUiKeys = mutableMapOf<String, ArrayList<Leg>>()
        oneForEachPartition.forEach { legsByUiKeys[timeUnit.toUiString(it)] = ArrayList() }
        for (leg in sortedLegs) {
            val timeUnitKey = timeUnit.toUiString(Converters.toLocalDateTime(leg.endTime))
            if (!legsByUiKeys.containsKey(timeUnitKey)) {
                println("No map entry for key $timeUnitKey")
                continue
            }
            legsByUiKeys[timeUnitKey]!!.add(leg)
        }

        // Assert that there are no two dates having the same uiStrings, but different uniqueStrings
        assert(oneForEachPartition.size == legsByUiKeys.keys.size)

        return legsByUiKeys
    }

    private fun handleAllTimeOption(sortedLegs: List<Leg>) {
        timeUnit = Quarter
        val minimumQuarters = 2
        timeUnitCount = minimumQuarters
        val now = LocalDateTime.now()
        val firstLegEnd = if (sortedLegs.isNotEmpty()) Converters.toLocalDateTime(sortedLegs[0].endTime) else LocalDateTime.now()
        while(now.minusMonths(timeUnitCount * 3L).isAfter(firstLegEnd)) {
            timeUnitCount++
        }
    }

    private fun getOneForEachPartition(): List<LocalDateTime> {
        val dateTimes = ArrayList<LocalDateTime>()
        val now = LocalDateTime.now()
        for (i in (timeUnitCount - 1) downTo 0) {
            dateTimes.add(timeUnit.afterGoingBack(i, now))
        }
        return dateTimes
    }
}