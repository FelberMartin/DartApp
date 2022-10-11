package com.development_felber.dartapp.util.graphs.partitioner

import com.development_felber.dartapp.data.persistent.database.Leg

interface LegPartitioner {

    /**
     * Expects the passed list of legs to be sorted by the timestamp.
     */
    fun partitionLegs(sortedLegs: List<Leg>) : Map<String, List<Leg>>
}