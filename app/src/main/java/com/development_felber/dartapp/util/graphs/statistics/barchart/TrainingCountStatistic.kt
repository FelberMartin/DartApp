package com.development_felber.dartapp.util.graphs.statistics.barchart

import com.development_felber.dartapp.data.persistent.database.leg.Leg

object TrainingCountStatistic : BarStatistic("Training Count") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }
}