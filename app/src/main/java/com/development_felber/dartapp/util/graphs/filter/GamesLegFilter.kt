package com.development_felber.dartapp.util.graphs.filter

import com.development_felber.dartapp.data.persistent.database.Leg
import com.development_felber.dartapp.util.graphs.partitioner.PartitionCountLegPartitioner

class GamesLegFilter constructor(
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