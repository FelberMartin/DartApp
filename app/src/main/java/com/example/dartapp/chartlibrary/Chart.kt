package com.example.dartapp.views.chart

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {

    private var linkedLegend: Legend? = null
    var colorManager = ColorManager.default

    var animatedEnter = true
    set(value) {
        field = value
        if (value == false) enterAnimation?.end()
    }
    protected open var enterAnimation: ObjectAnimator? = null

    protected val enterAnimationDuration = 600L


    var data: DataSet = DataSet()
        set(value) {
            val newData = value != field
            field = value
            if (newData) dataChanged()
        }

    var interactionEnabled = true
    protected var selectedIndex = -1
        set(value) {
            if (!interactionEnabled) return
            field = value
            onSelectionUpdate()
        }

    init {
        if (isInEditMode) {
            animatedEnter = false
        }
    }


    private fun dataChanged() {
        linkedLegend?.reload()

        selectedIndex = -1

        reload()
        invalidate()
    }

    open fun reload() {
        if (animatedEnter) enterAnimation?.start()
    }

    abstract fun onSelectionUpdate()

    fun link(legend: Legend) {
        linkedLegend = legend
        if (legend.linkedChart != this)
            legend.linkedChart = this
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP)
            return true

        if (!interactionEnabled) return true

        val index = getTouchedIndex(event.x, event.y)
        if (index == -1 || index == selectedIndex)
            selectedIndex = -1
        else
            selectedIndex = index

        invalidate()
        return true
    }

    abstract fun getTouchedIndex(x: Float, y: Float) : Int

    override fun onAnimationUpdate(p0: ValueAnimator) {
        invalidate()
    }
}