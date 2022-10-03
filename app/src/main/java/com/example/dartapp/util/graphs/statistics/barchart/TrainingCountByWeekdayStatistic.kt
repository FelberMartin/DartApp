package com.example.dartapp.util.graphs.statistics.barchart

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.graphs.partitioner.WeekdayLegPartitioner
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet

object TrainingCountByWeekdayStatistic : BarStatistic(
    name = "Training Count by Weekday",
    statisticSpecificPartitioner = WeekdayLegPartitioner()
) {

    private val WEEKDAY_ORDER = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }

    override fun sortDataPoints(dataPoints: List<DataPoint>): List<DataPoint> {
        return dataPoints.sortedBy { x -> WEEKDAY_ORDER.indexOf(x.xString(DataSet.Type.STRING)) }
    }
}