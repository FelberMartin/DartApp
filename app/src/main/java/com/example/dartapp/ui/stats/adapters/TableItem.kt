package com.example.dartapp.ui.stats.adapters

import com.example.dartapp.database.Leg
import com.example.dartapp.util.milliToDurationString

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
              milliToDurationString(it.sumOf { leg -> leg.durationMilli })
            },

            TableItem("Points/Serve", category = Category.AVERAGES) {
                String.format("%.1f", it.map { leg -> leg.servesAvg }.average())
            },
            TableItem("Double Quote", category = Category.AVERAGES) {
                String.format("%.0f%%", it.map { leg -> 100f / leg.doubleAttempts }.average())
            },
            TableItem("Avg. Checkout", category = Category.AVERAGES) {
                String.format("%.1f", it.map { leg -> leg.checkout }.average())
            },
            TableItem("Avg. Duration", category = Category.AVERAGES) {
                milliToDurationString(it.map { leg -> leg.durationMilli }.average().toLong())
            },

        )

    }
}