package com.example.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.example.dartapp.views.chart.util.DataSet

class ChartHolder@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    var chartType = EChartType.LINE_CHART
        set(value) {
            field = value; chartTypeChanged()
        }

    var dataSet = DataSet()
        set(value) {
            field = value; chart.data = value
        }

    private var _chart: Chart? = null
    val chart get() = _chart!!

    init {
        chartTypeChanged()
    }

    private fun chartTypeChanged() {
        this.removeView(_chart)
        _chart = when (chartType) {
            EChartType.LINE_CHART -> LineChart(context)
            EChartType.BAR_CHART -> BarChart(context)
            EChartType.PIE_CHART -> PieChart(context)
        }
        chart.data = dataSet
        this.addView(chart)
    }

}