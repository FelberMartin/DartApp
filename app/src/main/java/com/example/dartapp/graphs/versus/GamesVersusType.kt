package com.example.dartapp.graphs.versus

import com.example.dartapp.graphs.filter.GamesLegFilter
import com.example.dartapp.graphs.partitioner.PartitionCountLegPartitioner
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.LineChart

class GamesVersusType() : VersusTypeBase(
    "Games",
    GamesLegFilter(),
    PartitionCountLegPartitioner(10)
) {

    override fun modifyChart(chart: Chart) {
        if (chart is LineChart) {
            chart.showXAxisMarkers = false
            chart.showVerticalGrid = false
        }
    }
}

