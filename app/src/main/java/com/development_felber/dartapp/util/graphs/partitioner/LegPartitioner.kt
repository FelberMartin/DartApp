package com.development_felber.dartapp.util.graphs.partitioner

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg

interface LegPartitioner {

    /**
     * Expects the passed list of legs to be sorted by the timestamp.
     */
    fun partitionLegs(sortedLegs: List<FinishedLeg>) : Map<String, List<FinishedLeg>>
}