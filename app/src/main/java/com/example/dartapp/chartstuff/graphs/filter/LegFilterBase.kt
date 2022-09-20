package com.example.dartapp.chartstuff.graphs.filter

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.graphs.filter.FilterOption

abstract class LegFilterBase(
    val filterOptions: List<FilterOption>,
) {

    var filterOptionIndex = 0

    fun currentFilterOption() : FilterOption {
        return filterOptions[filterOptionIndex]
    }

    abstract fun filterLegs(legs: List<Leg>): List<Leg>

}