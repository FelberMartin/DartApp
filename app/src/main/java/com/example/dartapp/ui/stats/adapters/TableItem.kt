package com.example.dartapp.ui.stats.adapters

import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.util.categorizeServes
import com.example.dartapp.util.milliToDurationString

open class TableItem(
    val name: String,
    private val evaluator: ((List<Leg>) -> String)?,
) {

    fun getValue(legs: List<Leg>?): String {
        if (legs == null) return "No Data"
        return evaluator?.invoke(legs) ?: ""
    }

    companion object {
        private val upperItems = arrayListOf(
            // GENERAL
            TableHeader(TableHeader.Category.GENERAL),
            TableItem("#Games") { it.size.toString() },
            TableItem("#Darts") { (it.sumOf { leg -> leg.dartCount }).toString() },

            // TIME
            TableHeader(TableHeader.Category.TIME),
            TableItem("Time spent training") {
              milliToDurationString(it.sumOf { leg -> leg.durationMilli })
            },

            // AVERAGES
            TableHeader(TableHeader.Category.AVERAGES),
            TableItem("Points/Serve") {
                String.format("%.1f", it.map { leg -> leg.servesAvg }.average())
            },
            TableItem("Double Quote") {
                String.format("%.0f%%", it.map { leg -> 100f / leg.doubleAttempts }.average())
            },
            TableItem("Avg. Checkout") {
                String.format("%.1f", it.map { leg -> leg.checkout }.average())
            },
            TableItem("Avg. Duration") {
                milliToDurationString(it.map { leg -> leg.durationMilli }.average().toLong())
            },

            // SERVE DISTRIBUTION

        )

        fun items() : List<TableItem> {
            val items = upperItems
            items.add(TableHeader(TableHeader.Category.SERVE_DISTRIBUTION))
            items.addAll(distroItems())
            return items
        }

        private fun distroItems() : List<TableItem> {
            val categories = arrayListOf<Int>()
            repeat(10) { categories.add(it * 20) }

            val items = arrayListOf<TableItem>()
            for ((index, limit) in categories.withIndex()) {
                val s = if (limit == 180) "180" else "$limit+"
                val item = TableItem(s) {
                    it.sumOf { leg ->
                        val list = Converters.toArrayListOfInts(leg.servesList)
                        categorizeServes(categories, list)[s]!!}.toString()

                }
                items.add(item)
            }
            return items
        }

    }
}

class TableHeader(category: Category) : TableItem(category.toString(), null) {
    enum class Category {
        GENERAL,
        TIME,
        SERVE_DISTRIBUTION,
        AVERAGES,
    }
}