package com.example.dartapp.util.categorized_sort

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.ui.screens.history.CategorizedSortTypeBase


object DartCountCategorizedSortType : CategorizedSortTypeBase(name = "Dart Count", byDefaultDescending = false) {
    override val categories: List<Category>
        get() = listOf(
            Category("15 or less", 0),
            Category("21 or less", 16),
            Category("30 or less", 22),
            Category("more than 30", 31),
        )

    override fun valueForLeg(leg: Leg): Number {
        return leg.dartCount
    }
}