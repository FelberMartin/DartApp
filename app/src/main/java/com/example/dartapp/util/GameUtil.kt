package com.example.dartapp.util

import java.util.*

object GameUtil {

    const val MAX_VALUE_PER_SERVE = 180
    val DEFAULT_SERVE_CATEGORIES = listOf(0, 60, 100, 140, 180)

    // https://datagenetics.com/blog/november22021/index.html
    val INVALID_SERVES = listOf(179, 178, 176, 175, 173, 172, 169, 166, 163)

    /**
     * Counts for each category all the serves between the category bound (inclusive) and the next bigger category
     * bound (exclusive).
     */
    fun countServesForCategories(serves: List<Int>, categories: List<Int> = DEFAULT_SERVE_CATEGORIES): SortedMap<Int, Int> {
        val dividedServes = HashMap<Int, Int>()
        for ((index, limit) in categories.withIndex()) {
            var count = 0
            if (index < categories.size - 1)
                count = serves.count { s -> limit <= s && s < categories[index + 1] }
            else {
                count = serves.count { s -> limit <= s }
            }

            dividedServes[limit] = count
        }

        return dividedServes.toSortedMap()
    }

    fun nameServeCategory(categoryLimit: Int): String {
        return if (categoryLimit != 180) "$categoryLimit+" else "180"
    }



}