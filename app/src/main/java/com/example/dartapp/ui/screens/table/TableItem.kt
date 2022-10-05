package com.example.dartapp.ui.screens.table

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.GameUtil
import com.example.dartapp.util.extensions.toPrettyString

open class TableItem(
    val name: String,
    private val evaluator: ((List<Leg>) -> String)?,
) {

    fun getValue(legs: List<Leg>?): String {
        if (legs == null) return "No Data"
        val stringToShow = evaluator?.invoke(legs) ?: ""
        return stringToShow.replace("NaN", "-")
    }

    companion object {
        val totals = listOf(
            TableItem("#Games") { it.size.toString() },
            TableItem("#Darts") { (it.sumOf { leg -> leg.dartCount }).toString() },
            TableItem("Time spent training") {
                Converters.toDuration(it.sumOf { leg -> leg.durationSeconds }).toPrettyString()
            },
        )

        val averages = listOf(
            TableItem("Average") {
                String.format("%.1f", it.map { leg -> leg.servesAvg }.average())
            },
            TableItem("Avg (9 Darts)") {
                String.format("%.1f", it.map { leg -> leg.nineDartsAverage() }.average())
            },
            TableItem("Double Rate") {
                val doubleAttemptsAvg = it.map { leg -> leg.doubleAttempts }.average()
                if (doubleAttemptsAvg == 0.0) {
                    return@TableItem "-"
                }
                String.format("%.0f%%", 100.0 / doubleAttemptsAvg)
            },
            TableItem("Avg. Checkout") {
                String.format("%.1f", it.map { leg -> leg.checkout }.average())
            },
            TableItem("Avg. Duration") {
                Converters.toDuration(it.map { leg -> leg.durationSeconds }.average().toLong()).toPrettyString()
            },
            TableItem("Avg. Darts/Game") {
                String.format("%.1f", it.map { leg -> leg.dartCount }.average())
            }
        )

        val distribution = createDistributionItems()

        private fun createDistributionItems() : List<TableItem> {
            val categories = arrayListOf<Int>()
            repeat(10) { categories.add(it * 20) }

            val items = arrayListOf<TableItem>()
            for (limit in categories) {
                val itemName = if (limit == 180) "180" else "$limit+"
                val item = TableItem(itemName) {
                    it.sumOf { leg ->
                        val serves = Converters.toListOfInts(leg.servesList)
                        GameUtil.partitionSizeForLowerLimits(serves, categories)[limit]!!}.toString()
                }
                items.add(item)
            }
            return items
        }

    }
}
