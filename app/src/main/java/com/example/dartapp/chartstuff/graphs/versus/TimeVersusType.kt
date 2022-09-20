package com.example.dartapp.graphs.versus

import com.example.dartapp.chartstuff.graphs.filter.TimeLegFilter
import com.example.dartapp.chartstuff.graphs.partitioner.TimeLegPartitioner
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.LineChart


class TimeVersusType() : VersusTypeBase(
    "Time",
    TimeLegFilter(),
    TimeLegPartitioner()
) {

    override fun filterSeekBarIndexChanged(index: Int) {
        super.filterSeekBarIndexChanged(index)
        val pair = (legFilter?.currentFilterOption()?.value as Pair<Int, TimeUnit>)
        val timeLegPartitioner = legPartitioner as TimeLegPartitioner
        timeLegPartitioner.timeUnitCount = pair.first
        timeLegPartitioner.timeUnit = pair.second
    }

    override fun modifyChart(chart: Chart) {
        if (chart is LineChart) {
            chart.xMarkers.minDistance = 180f
            chart.showVerticalGrid = false
        }
    }
}