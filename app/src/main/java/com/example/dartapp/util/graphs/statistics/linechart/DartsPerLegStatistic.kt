package com.example.dartapp.util.graphs.statistics.linechart

import com.example.dartapp.data.persistent.database.Leg

object DartsPerLegStatistic : LineStatistic("Darts per Leg Avg") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg -> leg.dartCount }.average()
    }
}