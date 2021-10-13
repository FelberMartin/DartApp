package com.example.dartapp.views.chart

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
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

    var animatedEnter = true
    protected var enterDuration = 50f

    var animatedSelection = true
    protected var selectionDuration = 30f

    var data: DataSet = DataSet()
        set(value) {
            field = value
            dataChanged()
        }

    protected var selectedIndex = -1
        set(value) {
            field = value
            onSelectionUpdate()
        }


    protected open fun dataChanged() {
        linkedLegend?.reload()
    }



    abstract fun onSelectionUpdate()

    fun link(legend: Legend) {
        linkedLegend = legend
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP)
            return true

        val index = getTouchedIndex(event.x, event.y)
        if (index == -1 || index == selectedIndex)
            selectedIndex = -1
        else
            selectedIndex = index

        invalidate()
        return true
    }

    abstract fun getTouchedIndex(x: Float, y: Float) : Int

}