package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.Leg

object AverageStatistic: LineStatistic("Average") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg -> leg.average }.average()
    }
}