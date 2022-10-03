package com.example.dartapp.views.chart.legend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dartapp.views.chart.Chart
import com.example.dartapp.views.chart.PieChart
import com.example.dartapp.views.chart.util.DataSet
import kotlin.math.ceil


class Legend @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Describing how the entries should be patterned
    enum class Mode {
        STACKED,            // Simply stack one entry over the other
        AUTO_COLUMNED       // Try to make a table of entries, with as many columns as possible
    }

    // Indicator is the little colored icon in front of the text
    enum class IndicatorShape {
        CIRCLE, RECTANGLE
    }

    var mode = Mode.AUTO_COLUMNED
        set(value) {
            field = value
            recalculateLayout()
        }

    var indicatorShape: IndicatorShape = IndicatorShape.CIRCLE

    private var rows = 1
    private var columns: Int = 1
    private var desiredWidth: Int = 0
    private var desiredHeight: Int = 0

    // Link a Chart to display the legend entries corresponding to the chart's data
    var linkedChart: Chart? = null
        set(value) {
            field = value
            value?.link(this)
            reload()
        }

    private var entryTexts = ArrayList<String>()

    private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = Paint().apply {
        isAntiAlias = true
        color = Color(0xFF000000).toArgb()     // This color is set to match the colorManager of the linked chart.
        textSize = 40f
    }

    var textSize = 40f
        set(value) {
            field = value
            textSizeChanged()
        }

    private var indicatorSize: Float = 20f
    private var indicatorTopMargin = 5f
    // Spacing between the indicator and the beginning of the entry text label
    private var indicatorLabelSpacing = 10f

    // Spacing between the baselines of entries
    private var entrySpacingVertically = 40f
    // The minimum required spacing between the end of a entry text label and the next indicator
    private var entryMinSpacingHorizontally = 40f

    // Pixels from the top of the text to the bottom line
    // referring to [https://stackoverflow.com/a/27631737/13366254]
    private var textTopToBottom = 50f

    init {
        textSizeChanged()

        if (isInEditMode) {
            var chart = PieChart(context)
            chart.data = DataSet.random(type = DataSet.Type.STRING, count = 9)
            linkedChart = chart
        }
    }

    fun reload() {
        invalidate()
        initEntryTexts()
    }

    // Updates local variables depending on the new textSize
    private fun textSizeChanged() {
        textPaint.textSize = textSize
        val metrics = textPaint.fontMetrics
        textTopToBottom = metrics.bottom - metrics.top
        entryMinSpacingHorizontally = 2 * textSize
        entrySpacingVertically = 1.7f * textSize

        indicatorSize = 0.7f * textTopToBottom
        indicatorTopMargin = (textTopToBottom - indicatorSize) / 2
        indicatorLabelSpacing = 0.3f * textSize

        recalculateLayout()
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

        recalculateLayout()
    }

    // Handles the title of the DataPoints / DataSets depending on the linked charts type
    private fun initEntryTexts() {
        entryTexts = ArrayList()
        linkedChart?.data?.forEach {
            entryTexts.add(it.x.toString())
        }

        recalculateLayout()
    }

    // Responds to potential changes in the legends layout and sets the new values for rows,
    // columns and calculates the required dimensions
    private fun recalculateLayout() {
        if (entryTexts.size == 0) return

        val maxLabelWidth = entryTexts.maxOf { string -> textPaint.measureText(string) }
        val maxEntryWidth = maxLabelWidth + indicatorSize + indicatorLabelSpacing
        val newColumnWidth = maxEntryWidth + entryMinSpacingHorizontally
        var neededWidth = maxEntryWidth

        if (neededWidth > width) {
            Log.e("Chart Legend", "The labels cannot fit into the given width")
        }

        columns = 1
        if (mode == Mode.AUTO_COLUMNED) {
            // Calculate the maximal possible number of rows
            while (true) {
                if (neededWidth + newColumnWidth > width)
                    break

                neededWidth += newColumnWidth
                columns++
            }
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

        textPaint.color = linkedChart!!.colorManager.legendText

        val columnSpacing: Float = (width / columns).toFloat()

        for (row in 0 until rows) {
            canvas.save()
            for (column in 0 until columns) {
                val index = row * columns + column
                if (index >= entryTexts.size)
                    break

                // Indicator
                indicatorPaint.color = linkedChart!!.colorManager.getGraphColor(index)
                drawIndicator(canvas)

                // Text Label
                val x = indicatorSize + indicatorLabelSpacing
                val y = - textPaint.fontMetrics.top
                canvas.drawText(entryTexts[index], x, y, textPaint)


                canvas.translate(columnSpacing, 0f)
            }
            canvas.restore()

            canvas.translate(0f, entrySpacingVertically)
        }
    }

    // Draws the indicator icon
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