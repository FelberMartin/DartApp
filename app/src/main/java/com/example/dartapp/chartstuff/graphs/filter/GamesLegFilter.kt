package com.example.dartapp.chartstuff.graphs.filter

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.graphs.filter.FilterOption

class GamesLegFilter() : LegFilterBase(
    filterOptions = listOf(
        FilterOption("Last 10", 10),
        FilterOption("Last 100", 100),
        FilterOption("All", Int.MAX_VALUE)
    )
) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        val legCount = currentFilterOption().value as Int
        var start = legs.size - legCount
        if (start < 0) {
            start = 0
        }
        return legs.subList(start, legs.size)
    }
}