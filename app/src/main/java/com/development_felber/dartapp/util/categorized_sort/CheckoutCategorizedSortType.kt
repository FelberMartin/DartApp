package com.development_felber.dartapp.util.categorized_sort

import com.development_felber.dartapp.data.persistent.database.Leg
import com.development_felber.dartapp.ui.screens.history.CategorizedSortTypeBase

object CheckoutCategorizedSortType : CategorizedSortTypeBase(name = "Checkout", byDefaultDescending = true) {
    override val categories: List<Category>
        get() = listOf(
            Category("Over 100", 101),
            Category("Over 60", 61),
            Category("Over 40", 41),
            Category("40 or less", 40),
        )

    override fun valueForLeg(leg: Leg): Number {
        return leg.checkout
    }
}