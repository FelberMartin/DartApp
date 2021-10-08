package com.example.dartapp.views.chart.legend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.DataSet
import com.example.dartapp.views.chart.PieChart
import kotlin.math.ceil
import kotlin.math.floor


class Legend @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Mode {
        STACKED, AUTO_COLUMNED
    }

    enum class IndicatorShape {
        CIRCLE, RECTANGLE
    }

    var mode = Mode.AUTO_COLUMNED
        set(value) {
            field = value
            recalculateColumns()
        }

    var indicatorShape: IndicatorShape = IndicatorShape.CIRCLE

    private var rows = 1
    private var columns: Int = 1
    private var desiredWidth: Int = 0
    private var desiredHeight: Int = 0

    var linkedChart: Chart? = null
        set(value) {
            field = value
            invalidate()
            initEntryTexts()
        }

    private var entryTexts = ArrayList<String>()

    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 40f
    }

    var textSize = 40f
        set(value) {
            field = value
            textSizeChanged()
        }

    private var indicatorSize: Float = 20f
    private var indicatorTopMargin = 5f
    private var indicatorLabelSpacing = 10f

    private var entrySpacingVertically = 40f
    private var entryMinSpacingHorizontally = 40f

    private var textTopToBottom = 50f

    init {
        textSizeChanged()

        if (isInEditMode) {
            var chart = PieChart(context)
            chart.data = DataSet.random(type = DataSet.Type.STRING, count = 9)
            linkedChart = chart
        }
    }

    private fun textSizeChanged() {
        textPaint.textSize = textSize
        val metrics = textPaint.fontMetrics
        textTopToBottom = metrics.bottom - metrics.top
        entryMinSpacingHorizontally = 2 * textSize
        entrySpacingVertically = 1.7f * textSize

        indicatorSize = 0.7f * textTopToBottom
        indicatorTopMargin = (textTopToBottom - indicatorSize) / 2
        indicatorLabelSpacing = 0.3f * textSize

        recalculateColumns()
        invalidate()
    }

    // From: https://amryousef.me/custom-view-on-measure
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val requestedHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = when (requestedWidthMode) {
            MeasureSpec.EXACTLY -> requestedWidth
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> Math.min(requestedWidth, desiredWidth)
        }

        val height = when (requestedHeightMode) {
            MeasureSpec.EXACTLY -> requestedHeight
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> Math.min(requestedHeight, desiredHeight)
        }

        setMeasuredDimension(width, height)
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        recalculateColumns()
    }

    private fun initEntryTexts() {
        entryTexts = ArrayList()
        linkedChart?.data?.forEach {
            entryTexts.add(it.x.toString())
        }

        recalculateColumns()
    }

    private fun recalculateColumns() {
        if (mode == Mode.STACKED) return
        if (entryTexts.size == 0) return

        val maxLabelWidth = entryTexts.maxOf { string -> textPaint.measureText(string) }
        val maxEntryWidth = maxLabelWidth + indicatorSize + indicatorLabelSpacing
        val newColumnWidth = maxEntryWidth + entryMinSpacingHorizontally
        var neededWidth = maxEntryWidth

        if (neededWidth > width) {
            Log.e("Chart Legend", "The labels cannot fit into the given width")
        }

        columns = 1
        while (true) {
            if (neededWidth + newColumnWidth > width)
                break

            neededWidth += newColumnWidth
            columns++
        }

        rows = ceil(entryTexts.size / columns.toDouble()).toInt()

        desiredWidth = ceil(neededWidth).toInt()
        desiredHeight = ceil(textTopToBottom + entrySpacingVertically * (rows -1)).toInt()

        // Invalidates the current measurement and triggers onMeasure()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (linkedChart == null) return

        val columnSpacing: Float = (width / columns).toFloat()

        for (row in 0 until rows) {
            canvas.save()
            for (column in 0 until columns) {
                val index = row * columns + column
                if (index >= entryTexts.size)
                    break

                indicatorPaint.color = linkedChart!!.colorManager.get(index)
                drawIndicator(canvas)

                val x = indicatorSize + indicatorLabelSpacing
                val y = - textPaint.fontMetrics.top

                canvas.drawText(entryTexts[index], x, y, textPaint)

//                canvas.drawLine(0f, y, 100f, y, textPaint)
//                canvas.drawLine(20f, 0f, 20f, textSize, textPaint)

                canvas.translate(columnSpacing, 0f)
            }
            canvas.restore()

            canvas.translate(0f, entrySpacingVertically)
        }
    }

    private fun drawIndicator(canvas: Canvas) {
        canvas.save()
        canvas.translate(0f, indicatorTopMargin)
        if (indicatorShape == IndicatorShape.CIRCLE) {
            val radius = indicatorSize / 2
            canvas.translate(radius, radius)
            canvas.drawCircle(0f, 0f, radius, indicatorPaint)
        } else if (indicatorShape == IndicatorShape.RECTANGLE) {
            canvas.drawRect(0f, 0f, indicatorSize, indicatorSize, indicatorPaint)
        }

        canvas.restore()
    }




}