package com.example.dartapp.util.graphs.statistics.barchart

import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.util.graphs.partitioner.WeekdayLegPartitioner

object TrainingCountByWeekdayStatistic : BarStatistic(
    name = "Training Count by Weekday",
    statisticSpecificPartitioner = WeekdayLegPartitioner()
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }
}