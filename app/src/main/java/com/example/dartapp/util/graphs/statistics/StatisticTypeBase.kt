package com.example.dartapp.util.graphs.statistics

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.partitioner.LegPartitioner
import com.example.dartapp.util.graphs.statistics.barchart.TrainingCountByWeekdayStatistic
import com.example.dartapp.util.graphs.statistics.barchart.TrainingCountStatistic
import com.example.dartapp.util.graphs.statistics.linechart.AverageStatistic
import com.example.dartapp.util.graphs.statistics.linechart.DartsPerLegStatistic
import com.example.dartapp.util.graphs.statistics.linechart.FirstNineDartsAverageStatistic
import com.example.dartapp.util.graphs.statistics.linechart.LineStatistic
import com.example.dartapp.util.graphs.statistics.piechart.CheckoutDistributionStatistic
import com.example.dartapp.util.graphs.statistics.piechart.ServeDistributionStatistic
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.util.DataSet

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    val availableFilterCategories: List<LegFilterBase.Category> = listOf(LegFilterBase.Category.ByGameCount,
        LegFilterBase.Category.ByTime),
    protected val statisticSpecificPartitioner: LegPartitioner? = null
) {

    open fun modifyChart(chart: Chart) { }

    abstract fun buildDataSet(legs: List<Leg>, filter: LegFilterBase) : DataSet

    companion object {
        val all = listOf(
            AverageStatistic,
            FirstNineDartsAverageStatistic,
            DartsPerLegStatistic,
            ServeDistributionStatistic,
            TrainingCountStatistic,
            TrainingCountByWeekdayStatistic,
            CheckoutDistributionStatistic
        )
    }

    // This is added for the initialization of the StatisticsViewModel. For some reason the use of a normal statistic
    // at that place results in a NPE when calling the object :raised_eyebrow:
    object PlaceHolderStatistic : LineStatistic("") {
        override fun reduceLegsToNumber(legs: List<Leg>): Number {
            return 0
        }

    }
}

