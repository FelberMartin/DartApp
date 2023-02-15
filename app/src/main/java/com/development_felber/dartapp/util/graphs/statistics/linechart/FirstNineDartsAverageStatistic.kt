package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg

object FirstNineDartsAverageStatistic : LineStatistic("Average (first 9 Darts)") {

    override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
        return legs.map(FinishedLeg::nineDartsAverage).average()
    }
}