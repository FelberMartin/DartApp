package com.example.dartapp.util.graphs.statistics

import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.partitioner.WeekdayLegPartitioner
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.views.chart.BarChart
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.util.ColorManager

class TrainingCountByWeekday : StatisticTypeBase(
    "Training count by weekday",
    EChartType.BAR_CHART,
    availableFilterCategories = listOf(LegFilterBase.Category.ByTime),
    statisticSpecificPartitioner = WeekdayLegPartitioner()
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }

    override fun modifyChart(chart: Chart) {
        with(chart as BarChart) {
            xStartAtZero = false
            colorManager = ColorManager.singleColor
            showXAxisMarkers = true
            verticalAutoPadding = false
            topAutoPadding = true
        }
    }
}