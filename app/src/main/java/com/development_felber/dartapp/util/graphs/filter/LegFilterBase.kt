package com.development_felber.dartapp.util.graphs.filter

import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.util.graphs.partitioner.LegPartitioner

abstract class LegFilterBase(
    val name: String,
    val partitioner: LegPartitioner
) {

    abstract fun filterLegs(legs: List<Leg>): List<Leg>

    enum class Category(val displayedName: String, val filterOptions: List<LegFilterBase>) {
        ByGameCount("Games", GamesLegFilter.allFilters),
        ByTime("Time", TimeLegFilter.allFilters)
    }
}