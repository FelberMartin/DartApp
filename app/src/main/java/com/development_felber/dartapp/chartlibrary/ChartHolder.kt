package com.development_felber.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.development_felber.dartapp.views.chart.util.DataSet

class ChartHolder@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    var replaceChartOnDataSetChange = false

    var chartType = EChartType.LINE_CHART
        set(value) {
            field = value; replaceChart()
        }

    var dataSet = DataSet()
        set(value) {
            field = value
            if (replaceChartOnDataSetChange) { replaceChart() }
        }

    private var _chart: Chart? = null
    val chart get() = _chart!!

    init {
        replaceChart()
    }

    private fun replaceChart() {
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