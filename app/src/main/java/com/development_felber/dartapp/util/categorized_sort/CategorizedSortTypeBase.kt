package com.development_felber.dartapp.ui.screens.history

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.categorized_sort.CheckoutCategorizedSortType
import com.development_felber.dartapp.util.categorized_sort.DartCountCategorizedSortType
import com.development_felber.dartapp.util.categorized_sort.DateCategorizedSortType

abstract class CategorizedSortTypeBase(
    val name: String,
    val byDefaultDescending: Boolean
) {

    class Result() {
        private val map = mutableMapOf<Category, ArrayList<FinishedLeg>>()
        private val orderedCategories = mutableListOf<Category>()

        operator fun set(category: Category, leg: FinishedLeg) {
            if (map.containsKey(category)) {
                map[category]!!.add(leg)
            } else {
                orderedCategories.add(category)
                map[category] = arrayListOf(leg)
            }
        }

        operator fun get(category: Category) : List<FinishedLeg> {
            return map[category]!!
        }

        fun getOrderedCategories() : List<Category> {
            return orderedCategories
        }

        fun isEmpty() : Boolean = map.isEmpty()
    }

    data class Category(val name: String, val lowerInclusiveLimit: Number)
    abstract val categories: List<Category>

    fun sortLegsCategorized(legs: List<FinishedLeg>, descending: Boolean) : Result {
        val sorted = sortLegs(legs, descending)
        val result = Result()
        for (leg in sorted) {
            result[categorizeLeg(leg)] = leg
        }
        return result
    }

    private fun categorizeLeg(leg: FinishedLeg) : Category {
        val value = valueForLeg(leg).toDouble()
        val categoriesWithAscendingLimits = categories.sortedBy { category -> category.lowerInclusiveLimit.toDouble() }
        for ((index, category) in categoriesWithAscendingLimits.withIndex()) {
            val lowerInclusiveLimit = category.lowerInclusiveLimit.toDouble()
            if (index < categories.size - 1) {
                val nextBiggerLimit = categoriesWithAscendingLimits[index + 1].lowerInclusiveLimit.toDouble()
                if (lowerInclusiveLimit <= value && value < nextBiggerLimit) {
                    return category
                }
            } else {
                if (lowerInclusiveLimit <= value) {
                    return category
                }
            }
        }

        // Should never happen, should always return before.
        return categories.last()
    }

    private fun sortLegs(legs: List<FinishedLeg>, descending: Boolean) : List<FinishedLeg> {
        val sorted = legs.sortedBy { leg -> valueForLeg(leg).toDouble() }
        if (descending) {
            return sorted.reversed()
        }
        return sorted
    }

    abstract fun valueForLeg(leg: FinishedLeg) : Number



    object PlaceHolder: CategorizedSortTypeBase("PlaceHolder", false) {
        override val categories: List<Category>
            get() = listOf(Category("", 0))

        override fun valueForLeg(leg: FinishedLeg): Number {
            return 0
        }
    }

    companion object {
        val all = listOf(DateCategorizedSortType, DartCountCategorizedSortType, CheckoutCategorizedSortType)
    }
}