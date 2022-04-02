package com.example.dartapp.util

import java.util.*
import kotlin.collections.HashMap

object GameUtil {

    const val MAX_VALUE_PER_SERVE = 180

    /**
     * Counts for each category all the serves between the category bound (inclusive) and the next bigger category
     * bound (exclusive).
     */
    fun countServesForCategories(serves: List<Int>, categories: List<Int>): SortedMap<Int, Int> {
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

}