package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg

object AverageStatistic: LineStatistic("Average") {

    override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
        return legs.map { leg -> leg.average }.average()
    }
}