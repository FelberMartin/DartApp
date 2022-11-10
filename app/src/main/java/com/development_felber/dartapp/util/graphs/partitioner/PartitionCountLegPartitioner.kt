package com.development_felber.dartapp.util.graphs.partitioner

import com.development_felber.dartapp.data.persistent.database.leg.Leg
import kotlin.math.ceil

class PartitionCountLegPartitioner(
    private val partitionCount: Int,
    val startCountingAtOne: Boolean = true
) : LegPartitioner {

    override fun partitionLegs(sortedLegs: List<Leg>): Map<String, List<Leg>> {
        val map = mutableMapOf<String, List<Leg>>()
        val partitionSize = calculatePartitionSize(sortedLegs)
        var index = 0
        while (index < sortedLegs.size) {
            var toIndex = index + partitionSize
            if (toIndex > sortedLegs.size) {
                toIndex = sortedLegs.size
            }
            val partitionName = getPartitionName(index, toIndex - 1)
            map[partitionName] = sortedLegs.subList(index, toIndex)
            index = toIndex
        }
        return map
    }

    private fun getPartitionName(fromIndex: Int, toIndex: Int): String {
        val from = if (startCountingAtOne) fromIndex + 1 else fromIndex
        val to = if (startCountingAtOne) toIndex + 1 else toIndex

        if (fromIndex == toIndex) {
            return "$from"
        }

        return "$from-$to"
    }

    private fun calculatePartitionSize(legs: List<Leg>): Int {
        var partitionSize = legs.size.toDouble() / partitionCount.toDouble()
        if (partitionSize <= 0.0) {
            partitionSize = 1.0
        }
        return ceil(partitionSize).toInt()
    }
}