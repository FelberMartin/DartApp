package com.example.dartapp.graphs.partitioner

import com.example.dartapp.database.Leg

interface LegPartitioner {

    /**
     * Expects the passed list of legs to be sorted by the timestamp.
     */
    fun partitionLegs(sortedLegs: List<Leg>) : Map<String, List<Leg>>
}