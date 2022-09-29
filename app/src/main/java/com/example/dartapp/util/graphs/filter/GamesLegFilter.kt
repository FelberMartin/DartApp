package com.example.dartapp.util.graphs.filter

import com.example.dartapp.util.graphs.partitioner.PartitionCountLegPartitioner
import com.example.dartapp.data.persistent.database.Leg

class GamesLegFilter private constructor(
    name: String,
    val legCount: Int
) : LegFilterBase(
    name = name,
    partitioner = PartitionCountLegPartitioner(10)
) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        var start = legs.size - legCount
        if (start < 0) {
            start = 0
        }
        return legs.subList(start, legs.size)
    }

    companion object {
        val last10 = GamesLegFilter("Last 10", 10)
        val last100 = GamesLegFilter("Last 100", 100)
        val all = GamesLegFilter("All", Int.MAX_VALUE)

        val allFilters = listOf(last10, last100, all)
    }
}