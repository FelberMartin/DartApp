package com.example.dartapp.graphs.partitioner

import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.filter.TimeLegFilter
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.util.time.units.Day
import com.example.dartapp.util.time.units.Quarter
import java.time.LocalDateTime


class TimeLegPartitioner() : LegPartitioner {

    var timeUnit: TimeUnit = Day
    var timeUnitCount: Int = 1

    override fun partitionLegs(sortedLegs: List<Leg>): Map<String, List<Leg>> {
        if (timeUnitCount == TimeLegFilter.ALL_TIME_COUNT) {
            handleAllTimeOption(sortedLegs[0])
        }
        val oneForEachPartition = getOneForEachPartition()
        val legsByUiKeys = mutableMapOf<String, ArrayList<Leg>>()
        oneForEachPartition.forEach { legsByUiKeys[timeUnit.toUiString(it)] = ArrayList() }
        for (leg in sortedLegs) {
            val timeUnitKey = timeUnit.toUiString(Converters.toLocalDateTime(leg.endTime))
            legsByUiKeys[timeUnitKey]!!.add(leg)
        }

        // Assert that there are no two dates having the same uiStrings, but different uniqueStrings
        assert(oneForEachPartition.size == legsByUiKeys.keys.size)

        return legsByUiKeys
    }

    private fun handleAllTimeOption(firstLeg: Leg) {
        timeUnit = Quarter
        val minimumQuarters = 2
        timeUnitCount = minimumQuarters
        val now = LocalDateTime.now()
        val firstLegEnd = Converters.toLocalDateTime(firstLeg.endTime)
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