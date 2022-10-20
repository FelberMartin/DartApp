package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.runtime.Composable


@Composable
fun PieChart(
    dataSet: DataSet,
    animateDataSetChange: Boolean = true,
    selectionEnabled: Boolean = true,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig(),
    colorSequence: ColorSequence = ChartDefaults.colorSequence()
) {
    
}