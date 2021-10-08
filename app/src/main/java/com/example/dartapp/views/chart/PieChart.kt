package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val PIE_OFFSET = 10f
private const val STARTING_ANGLE = -90f

private const val TAG = "PieChart"

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    override var data: DataSet = DataSet()
        set(value) {
            field = value
            dataChanged()
        }

    private var dataSum: Float = 0.0f
    private lateinit var fractions: ArrayList<Float>
    private lateinit var startPoints: ArrayList<PointF>
    private lateinit var middlePoints: ArrayList<PointF>

    private lateinit var centeredCircle: RectF


    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }


    init {
        if (true) {
            data = DataSet.Generator.random(type = DataSet.Type.STRING, count=3)
        }
    }

    private fun dataChanged() {
        data.forEach{ dp -> assert(dp.y.toDouble() >= 0) }
        dataSum = data.sumOf { dp -> dp.y.toDouble() }.toFloat()

        fractions = ArrayList()
        data.forEach { dp -> fractions.add(dp.y.toFloat() / dataSum) }

        recalculatePoints()
    }

    private fun recalculatePoints() {
        startPoints = ArrayList()
        middlePoints = ArrayList()

        var startAngle = STARTING_ANGLE * 2 * PI / 360
        for (fraction in fractions) {
            startPoints.add(PointF(cos(startAngle).toFloat(), sin(startAngle).toFloat()))
            val fractionAngle = fraction * 2 * PI
            val middleAngle = startAngle + fractionAngle / 2
            middlePoints.add(PointF(cos(middleAngle).toFloat(), sin(middleAngle).toFloat()))
            startAngle += fractionAngle
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val desiredWidth = requestedWidth + paddingLeft + paddingRight
        val desiredHeight = requestedHeight + paddingTop + paddingBottom

        val size = min(desiredWidth, desiredHeight)

        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val realOffset = PIE_OFFSET
        val size = min(w, h)
        var left = realOffset
        var top = realOffset
        var right = size - realOffset
        var bottom = size - realOffset
        centeredCircle = RectF(left, top, right, bottom)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // Pie
        var startAngle = STARTING_ANGLE
        for (index in 0 until data.size) {
            val sweepAngle = fractions[index] * 360
            paint.color = colorManager.get(index)
            canvas.drawArc(centeredCircle, startAngle, sweepAngle, true, paint)

            startAngle += sweepAngle
        }

        // Spaces between fractions
        paint.color = Color.WHITE
        for (point in startPoints) {
            val radius = width / 2f
            val relX = point.x * radius
            val relY = point.y * radius
            canvas.drawLine(centerX, centerY,
                centerX + relX, centerY + relY, paint)
        }

    }

    




}