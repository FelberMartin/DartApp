package com.example.dartapp.ui.stats.adapters

import androidx.annotation.StringRes
import com.example.dartapp.R
import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.util.GameUtil
import com.example.dartapp.util.NumberFormatter
import com.example.dartapp.util.Strings

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
        private val totalsAndAverages = arrayListOf(
            // TOTALS
            TableHeader(TableHeader.Category.TOTALS),
            TableItem("#Games") { it.size.toString() },
            TableItem("#Darts") { (it.sumOf { leg -> leg.dartCount }).toString() },

            TableItem("Time spent training") {
              NumberFormatter.milliToDurationString(it.sumOf { leg -> leg.durationMilli })
            },

            // AVERAGES
            TableHeader(TableHeader.Category.AVERAGES),
            TableItem("Avg. Points/Serve") {
                String.format("%.1f", it.map { leg -> leg.servesAvg }.average())
            },
            TableItem("Double Quote") {
                String.format("%.0f%%", it.map { leg -> 100f / leg.doubleAttempts }.average())
            },
            TableItem("Avg. Checkout") {
                String.format("%.1f", it.map { leg -> leg.checkout }.average())
            },
            TableItem("Avg. Duration") {
                NumberFormatter.milliToDurationString(it.map { leg -> leg.durationMilli }.average().toLong())
            },
            TableItem("Avg. Darts/Game") {
                String.format("%.1f", it.map { leg -> leg.dartCount }.average())
            }
        )

        fun items() : List<TableItem> {
            val items = ArrayList(totalsAndAverages)
            items.add(TableHeader(TableHeader.Category.SERVE_DISTRIBUTION))
            items.addAll(distributionItems())
            return items
        }

        private fun distributionItems() : List<TableItem> {
            val categories = arrayListOf<Int>()
            repeat(10) { categories.add(it * 20) }

            val items = arrayListOf<TableItem>()
            for (limit in categories) {
                val itemName = if (limit == 180) "180" else "$limit+"
                val item = TableItem(itemName) {
                    it.sumOf { leg ->
                        val serves = Converters.toListOfInts(leg.servesList)
                        GameUtil.countServesForCategories(serves, categories)[limit]!!}.toString()
                }
                items.add(item)
            }
            return items
        }

    }
}

class TableHeader(category: Category) : TableItem(Strings.get(category.stringRes), null) {
    enum class Category(@StringRes val stringRes: Int) {
        TOTALS(R.string.totals),
        SERVE_DISTRIBUTION(R.string.serve_distribution),
        AVERAGES(R.string.averages),
    }
}