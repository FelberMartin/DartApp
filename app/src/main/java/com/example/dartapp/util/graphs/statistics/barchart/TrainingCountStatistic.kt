package com.example.dartapp.util.graphs.statistics.barchart

import com.example.dartapp.data.persistent.database.Leg

object TrainingCountStatistic : BarStatistic("Training Count") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }
}