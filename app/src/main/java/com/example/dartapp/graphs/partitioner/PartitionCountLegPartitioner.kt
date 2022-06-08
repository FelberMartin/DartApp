package com.example.dartapp.graphs.partitioner

import com.example.dartapp.database.Leg

class PartitionCountLegPartitioner(
    private val partitionCount: Int
) : LegPartitioner {

    override fun partitionLegs(sortedLegs: List<Leg>): Map<Any, List<Leg>> {
        val map = mutableMapOf<Any, List<Leg>>()
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
        if (fromIndex == toIndex) {
            return "$fromIndex"
        }

        return "$fromIndex-$toIndex"
    }

    private fun calculatePartitionSize(legs: List<Leg>): Int {
        var partitionSize = legs.size / partitionCount
        if (partitionSize <= 0) {
            partitionSize = 1
        }
        return partitionSize
    }
}