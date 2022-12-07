package com.development_felber.dartapp.util.categorized_sort

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.ui.screens.history.CategorizedSortTypeBase


object DartCountCategorizedSortType : CategorizedSortTypeBase(name = "Dart Count", byDefaultDescending = false) {
    override val categories: List<Category>
        get() = listOf(
            Category("15 or less", 0),
            Category("21 or less", 16),
            Category("30 or less", 22),
            Category("more than 30", 31),
        )

    override fun valueForLeg(leg: FinishedLeg): Number {
        return leg.dartCount
    }
}