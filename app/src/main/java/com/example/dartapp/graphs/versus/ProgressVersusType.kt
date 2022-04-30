package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.example.dartapp.views.chart.util.DataSet

class ProgressVersusType(
    private val backCountLimit: Int
) : VersusTypeBase(makeName(backCountLimit), DataSet.Type.NUMBER) {

    override fun filterLegs(legs: List<Leg>): List<Leg> {
        var start = legs.size - backCountLimit
        if (start < 0) {
            start = 0
        }
        return legs.subList(start, legs.size)
    }

    override fun partitionLegs(legs: List<Leg>): Map<Any, List<Leg>> {
        val map = HashMap<Any, List<Leg>>()
        legs.forEachIndexed { index, leg -> map[index] = listOf(leg) }
        return map
    }

    companion object {
        fun makeName(limit: Int) : String {
            if (limit == Int.MAX_VALUE) {
                return "All legs"
            }
            return "Last $limit legs"
        }
    }
}

