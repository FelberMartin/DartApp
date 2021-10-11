package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors

private const val TAG = "LineChart"

class LineChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinateBasedChart(context, attrs, defStyleAttr) {

    private var linePath = Path()
    var smoothedLine = true
    var animated = true
    private var frame = 0
    private var interpolator = AccelerateDecelerateInterpolator()

    val textShader: Shader = LinearGradient(0f, 0f, 0f, 500f, intArrayOf(
        Color.parseColor("#F97C3C"),
        Color.parseColor("#FDB54E"),
        Color.parseColor("#64B678"),
        Color.parseColor("#478AEA"),
        Color.parseColor("#8446CC")
    ), null, Shader.TileMode.CLAMP)

    private val linePaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = MaterialColors.getColor(this@LineChart, R.attr.colorPrimary)
        shader = textShader
    }

    init {
        if (isInEditMode) {
            drawCoordRect = true
            animated = false
        }
        data = DataSet.random(count = 8, randomX = true)
    }

    fun reload() {
        dataChanged()
        frame = 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updatePath()
    }

    override fun dataChanged() {
        super.dataChanged()

        data.forEach { dp -> Log.d(TAG, "${dp.x}, ${dp.y}") }

        updatePath()
        frame = 0
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLines(canvas)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawLines(canvas: Canvas) {
        val animationEnd = if (animated) 50f else 0f
        if (frame >= animationEnd)
            canvas.drawPath(linePath, linePaint)
        else {
            val fraction = interpolator.getInterpolation(frame/animationEnd)
            val appr = linePath.approximate(0.5f)
            val points = appr.filterIndexed { index, _ -> index % 3 != 0 }
            val fractions = appr.filterIndexed { index, _ -> index % 3 == 0 }
            var lines = ArrayList<Float>()
            for (i in 0 until points.size / 2) {
                val x = points[2 * i]
                val y = points[2 * i + 1]
                lines.add(x)
                lines.add(y)
                if (i != 0 && i != points.size / 2 - 1) {
                    lines.add(x)
                    lines.add(y)
                }
                if (fractions[i] > fraction)
                    break
            }
            canvas.drawLines(lines.toFloatArray(), linePaint)
            frame++
            invalidate()
        }

        // Circles around data points
        for (i in 0 until data.size) {
            val p = inCoordSystem(i)
//            canvas.drawCircle(p.x, p.y, 5f, linePaint)
        }
    }
}