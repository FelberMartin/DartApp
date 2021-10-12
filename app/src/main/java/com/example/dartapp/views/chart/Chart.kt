package com.example.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.dartapp.views.chart.legend.Legend
import com.example.dartapp.views.chart.util.ColorManager
import com.example.dartapp.views.chart.util.DataSet


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

    protected var selectedIndex = -1
        set(value) {
            field = value
            if (value != -1) updateSelectionInfo()
        }

    abstract fun updateSelectionInfo()

    fun link(legend: Legend) {
        linkedLegend = legend
    }


}