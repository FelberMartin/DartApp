package com.example.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.dartapp.views.chart.legend.Legend
import com.example.dartapp.views.chart.testfragments.LegendTestFragment


abstract class Chart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var linkedLegend: Legend? = null
    var colorManager = ColorManager()

    var data: DataSet = DataSet()
        set(value) {
            field = value
            dataChanged()
        }

    protected open fun dataChanged() {
        linkedLegend?.reload()
    }

    fun link(legend: Legend) {
        linkedLegend = legend
    }


}