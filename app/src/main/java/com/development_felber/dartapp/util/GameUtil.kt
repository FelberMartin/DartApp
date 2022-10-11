package com.development_felber.dartapp.util

import java.util.*

object GameUtil {

    // https://datagenetics.com/blog/november22021/index.html
    val INVALID_SERVES = listOf(179, 178, 176, 175, 173, 172, 169, 166, 163)


    /**
     * Counts for each limit all the numbers between the limit bound (inclusive) and the next bigger limit
     * bound (exclusive).
     */
    fun partitionSizeForLowerLimits(numbersToPartition: List<Int>, limits: List<Int>): SortedMap<Int, Int> {
        val partitionSizeByLimit = HashMap<Int, Int>()
        for ((index, limit) in limits.withIndex()) {
            var count = 0
            if (index < limits.size - 1)
                count = numbersToPartition.count { s -> limit <= s && s < limits[index + 1] }
            else {
                count = numbersToPartition.count { s -> limit <= s }
            }

            partitionSizeByLimit[limit] = count
        }

        return partitionSizeByLimit.toSortedMap()
    }

    fun nameServeCategory(categoryLimit: Int): String {
        return if (categoryLimit != 180) "$categoryLimit+" else "180"
    }

    fun minDartCountRequiredToFinishWithinServe(points: Int): Int? {
        if (points == 50) {
            // The CheckoutTip here is "S10D20", but the fastest is Bullseye.
            return 1
        }
        val tip = CheckoutTip.checkoutTips[points] ?: return null
        return tip.count { c -> c == ',' } + 1
    }

}