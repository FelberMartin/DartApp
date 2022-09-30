package com.example.dartapp.util.graphs.filter

import com.example.dartapp.util.graphs.partitioner.LegPartitioner
import com.example.dartapp.data.persistent.database.Leg

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