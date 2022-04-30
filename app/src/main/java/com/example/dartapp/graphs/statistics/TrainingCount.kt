package com.example.dartapp.graphs.statistics

import com.example.dartapp.database.Leg
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.graphs.versus.WeekdayVersusType
import com.example.dartapp.views.chart.EChartType

class TrainingCount() : StatisticTypeBase(
    "Training count",
    EChartType.BAR_CHART,
    TimeVersusType.defaults, WeekdayVersusType.defaults
) {

    override fun reduceLegsToNumber(legs: List<Leg>): Number {
        return legs.size
    }
}