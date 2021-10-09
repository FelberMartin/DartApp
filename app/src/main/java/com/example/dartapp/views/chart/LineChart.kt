package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors

private const val TAG = "LineChart"

class LineChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinateBasedChart(context, attrs, defStyleAttr) {

    private var linePath = Path()
    var smoothedLine = true

    private val linePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = MaterialColors.getColor(this@LineChart, R.attr.colorPrimary)
    }

    init {
        if (isInEditMode) {
            drawCoordRect = true
        }
        data = DataSet.random(count = 10)
    }

    fun reload() {
        dataChanged()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updatePath()
    }

    override fun dataChanged() {
        super.dataChanged()

        data.forEach { dp -> Log.d(TAG, "${dp.x}, ${dp.y}") }

        updatePath()
        invalidate()
    }

    private fun updatePath() {
        linePath.reset()
        var lastPoint = inCoordSystem(0)
        var control = lastPoint
        linePath.moveTo(lastPoint.x, lastPoint.y)
        for (i in 0 until data.size) {
            val nextPoint = inCoordSystem(i)

            if (smoothedLine) {
                control = nextPoint.plus(lastPoint)
                linePath.quadTo(lastPoint.x, lastPoint.y,
                    control.x / 2, control.y / 2)
            } else
                linePath.lineTo(nextPoint.x, nextPoint.y)
            lastPoint = nextPoint
        }
        if (smoothedLine)
            linePath.quadTo(control.x / 2, control.y / 2, lastPoint.x, lastPoint.y)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLines(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        canvas.drawPath(linePath, linePaint)
        for (i in 0 until data.size) {
            val p = inCoordSystem(i)
            canvas.drawCircle(p.x, p.y, 5f, linePaint)
        }
    }
}