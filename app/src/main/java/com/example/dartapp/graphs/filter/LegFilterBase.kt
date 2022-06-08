package com.example.dartapp.graphs.filter

import com.example.dartapp.database.Leg

abstract class LegFilterBase(
    val filterOptions: List<FilterOption>,
) {

    var filterOptionIndex = 0

    fun currentFilterOption() : FilterOption {
        return filterOptions[filterOptionIndex]
    }

    abstract fun filterLegs(legs: List<Leg>): List<Leg>

}