package com.example.dartapp.util.graphs.statistics.linechart

import com.example.dartapp.data.persistent.database.Leg

object AverageStatistic: LineStatistic("Average") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg -> leg.average }.average()
    }
}