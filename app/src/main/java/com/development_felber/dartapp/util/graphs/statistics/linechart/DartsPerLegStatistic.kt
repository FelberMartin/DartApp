package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg

object DartsPerLegStatistic : LineStatistic("Darts per Leg Avg") {

    override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
        return legs.map { leg -> leg.dartCount }.average()
    }
}