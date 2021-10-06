package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import kotlin.math.PI
import kotlin.math.min

private const val PIE_OFFSET = 10f

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    override var data: DataSet = DataSet()
        set(value) {
            field = value
            field.forEach{ dp -> assert(dp.y.toDouble() >= 0) }
            dataSum = field.sumOf { dp -> dp.y.toDouble() }.toFloat()
        }
    private var dataSum: Float = 0.0f

    private lateinit var oval: RectF

    private val paint: Paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        if (true) {
            data = DataSet.Generator.random(count=3)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val size = min(w, h)
        var left = PIE_OFFSET
        var top = PIE_OFFSET
        var right = size - 2 * PIE_OFFSET
        var bottom = size - 2 * PIE_OFFSET
        oval = RectF(left, top, right, bottom)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var startAngle = 0f
        for ((index, dp) in data.withIndex()) {
            val fraction = dp.y.toFloat() / dataSum
            val sweepAngle = fraction * 360

            paint.color = colorManager.get(index)
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint)

            startAngle += sweepAngle
        }
    }




}