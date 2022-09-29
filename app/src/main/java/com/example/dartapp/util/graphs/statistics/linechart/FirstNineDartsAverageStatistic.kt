package com.example.dartapp.util.graphs.statistics.linechart

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg

object FirstNineDartsAverageStatistic : LineStatistic("Average (first 9 Darts)") {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.map { leg ->
            val serves = Converters.toListOfInts(leg.servesList)
            serves.subList(0, 3).average()
        }.average()
    }
}