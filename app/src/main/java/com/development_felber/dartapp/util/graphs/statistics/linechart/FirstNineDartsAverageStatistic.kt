package com.development_felber.dartapp.util.graphs.statistics.linechart

import com.development_felber.dartapp.data.persistent.database.Leg

object FirstNineDartsAverageStatistic : LineStatistic("Average (first 9 Darts)") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map(Leg::nineDartsAverage).average()
    }
}