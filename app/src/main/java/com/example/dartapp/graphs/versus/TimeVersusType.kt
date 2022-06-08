package com.example.dartapp.graphs.versus

import com.example.dartapp.graphs.filter.TimeLegFilter
import com.example.dartapp.graphs.partitioner.TimeLegPartitioner
import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.LineChart


class TimeVersusType() : VersusTypeBase(
    "Time",
    TimeLegFilter(),
    TimeLegPartitioner()
) {

    override fun filterIndexChanged(index: Int) {
        super.filterIndexChanged(index)
        val timeUnit = (legFilter?.currentFilterOption()?.value as Pair<*, *>).second
        (legPartitioner as TimeLegPartitioner).timeUnit = timeUnit as TimeUnit
    }

    override fun modifyChart(chart: Chart) {
        if (chart is LineChart) {
            chart.xMarkers.minDistance = 180f
            chart.showVerticalGrid = false
        }
    }
}