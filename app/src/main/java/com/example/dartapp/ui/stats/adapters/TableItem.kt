package com.example.dartapp.ui.stats.adapters

import com.example.dartapp.database.Leg

class TableItem(
    val name: String,
    var category: Category = Category.GENERAL,
    val evaluator: ((List<Leg>) -> String),
) {

    enum class Category {
        GENERAL,
        TIME,
        SERVE_DISTRIBUTION,
        AVERAGES,
    }

    fun getValue(legs: List<Leg>?): String {
        if (legs == null) return "No Data"
        return evaluator.invoke(legs)
    }

    companion object {
        val items = listOf(
            TableItem("#Games") { it.size.toString() },
            TableItem("#Darts") { (it.sumOf { leg -> leg.dartCount }).toString() },

            TableItem("Time spent training", category = Category.TIME) {
              it.sumOf { leg -> leg.durationMilli }.toString()
            },
        )
    }
}