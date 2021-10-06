package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.ceil
import kotlin.math.floor

private const val INDICATOR_RADIUS = 15f
private const val INDICATOR_WIDTH = 25f
private const val OFFSET = 10f

class Legend @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Mode {
        STACKED, AUTO_COLUMNED
    }

    var mode = Mode.AUTO_COLUMNED
        set(value) {
            field = value
            recalculateColumns()
        }
    private var columns: Int = 1

    var linkedChart: Chart? = null
        set(value) {
            field = value
            invalidate()
            initLabels()
        }

    private var labels: ArrayList<String> = ArrayList()

    private var indicatorPaint = Paint().apply {
        isAntiAlias = true
    }

    private var textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textSize = 40f
    }

    private var textSize = 20f
        set(value) {
            field = value
            textPaint.textSize = field
        }

    private var labelSpacingVertically = 40f
        get() = textSize * 2

    private var labelMinSpacingHorizontally = 40f


    init {
        if (isInEditMode) {
            var chart = PieChart(context)
            chart.data = DataSet.random(type = DataSet.Type.STRING, count = 9)
            linkedChart = chart
        }
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        recalculateColumns()
    }

    private fun initLabels() {
        labels = ArrayList()
        linkedChart?.data?.forEach {
            labels.add(it.x.toString())
        }

        recalculateColumns()
    }

    private fun recalculateColumns() {
        if (mode == Mode.STACKED) return
        if (labels.size == 0) return

        val maxLabelWidth = labels.maxOf { string -> textPaint.measureText(string) }
        val maxEntryWidth = maxLabelWidth + INDICATOR_WIDTH
        var neededWidth = maxEntryWidth

        columns = 0
        while (neededWidth <= width) {
            neededWidth += labelMinSpacingHorizontally + maxEntryWidth
            columns++
        }

        if (columns == 0) {
            Log.e("Chart Legend", "The labels cannot fit into the given width")
        }

        // TODO move this to onMeasure()
        // TODO for testing make Stats with TabsHost
        val rows = ceil(labels.size / columns.toDouble())
        val requiredHeight = 2 * OFFSET + labelSpacingVertically * rows

        setMeasuredDimension(width, requiredHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (linkedChart == null) return

        canvas.translate(OFFSET, OFFSET)
        val columnSpacing = INDICATOR_WIDTH + (width / columns)

        for ((index, string) in labels.withIndex()) {
            val row: Int = index / columns
            val column = index % columns

            var x: Float = INDICATOR_RADIUS + column * columnSpacing
            var y: Float = INDICATOR_RADIUS + row * labelSpacingVertically

            indicatorPaint.color = linkedChart!!.colorManager.get(index)
            canvas.drawCircle(x, y, INDICATOR_RADIUS, indicatorPaint)

            x += INDICATOR_WIDTH
            y += INDICATOR_RADIUS

            canvas.drawText(string, x, y, textPaint)
        }
    }
}