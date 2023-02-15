package com.development_felber.dartapp.util.graphs.statistics.barchart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg

object TrainingCountStatistic : BarStatistic("Training Count") {

    override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
        return legs.size
    }
}