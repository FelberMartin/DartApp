package com.development_felber.dartapp.util.graphs.statistics.barchart

import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.util.graphs.partitioner.WeekdayLegPartitioner
import com.development_felber.dartapp.views.chart.data.DataPoint
import com.development_felber.dartapp.views.chart.util.DataSet

object TrainingCountByWeekdayStatistic : BarStatistic(
    name = "Training Count by Weekday",
    statisticSpecificPartitioner = WeekdayLegPartitioner()
) {

    private val WEEKDAY_ORDER = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun reduceLegsToNumber(legs: List<FinishedLeg>): Number {
        return legs.size
    }

    override fun sortDataPoints(dataPoints: List<DataPoint>): List<DataPoint> {
        return dataPoints.sortedBy { x -> WEEKDAY_ORDER.indexOf(x.xString(DataSet.Type.STRING)) }
    }
}