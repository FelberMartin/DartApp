package com.example.dartapp.graphs.versus

import com.example.dartapp.chartstuff.graphs.filter.LegFilterBase
import com.example.dartapp.chartstuff.graphs.partitioner.LegPartitioner
import com.example.dartapp.views.chart.Chart

abstract class VersusTypeBase (
    val name: String,
    val legFilter: LegFilterBase?,
    val legPartitioner: LegPartitioner
){

    open fun filterSeekBarIndexChanged(index: Int) {
        if (legFilter?.filterOptions?.size ?: 0 <= index) {
            return
        }
        legFilter?.filterOptionIndex = index
    }

    open fun modifyChart(chart: Chart) {}

}