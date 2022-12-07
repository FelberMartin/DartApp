package com.development_felber.dartapp.util.graphs.statistics

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.partitioner.LegPartitioner
import com.development_felber.dartapp.util.graphs.statistics.barchart.TrainingCountByWeekdayStatistic
import com.development_felber.dartapp.util.graphs.statistics.barchart.TrainingCountStatistic
import com.development_felber.dartapp.util.graphs.statistics.linechart.AverageStatistic
import com.development_felber.dartapp.util.graphs.statistics.linechart.DartsPerLegStatistic
import com.development_felber.dartapp.util.graphs.statistics.linechart.FirstNineDartsAverageStatistic
import com.development_felber.dartapp.util.graphs.statistics.linechart.LineStatistic
import com.development_felber.dartapp.util.graphs.statistics.piechart.CheckoutDistributionStatistic
import com.development_felber.dartapp.util.graphs.statistics.piechart.ServeDistributionStatistic
import com.development_felber.dartapp.views.chart.Chart
import com.development_felber.dartapp.views.chart.EChartType
import com.development_felber.dartapp.views.chart.util.DataSet

abstract class StatisticTypeBase (
    val name: String,
    val chartType: EChartType,
    val availableFilterCategories: List<LegFilterBase.Category> = listOf(LegFilterBase.Category.ByGameCount,
        LegFilterBase.Category.ByTime),
    protected val statisticSpecificPartitioner: LegPartitioner? = null
) {

    open fun modifyChart(chart: Chart) { }

    abstract fun buildDataSet(legs: List<FinishedLeg>, filter: LegFilterBase) : DataSet

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
        override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
            return 0
        }

    }
}

