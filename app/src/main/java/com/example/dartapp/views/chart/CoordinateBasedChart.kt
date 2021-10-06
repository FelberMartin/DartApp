package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

private const val ARROW_STRENGTH = 5f
private const val ARROW_LINES_COUNT = 2 * 3     // For each axis 3 lines


abstract class CoordinateBasedChart(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {


    private val arrowOffset: Float = 20f
    private var xArrowLength: Float = 0f
    private var yArrowLength: Float = 0f

    private var arrowLines: FloatArray = FloatArray(ARROW_LINES_COUNT * 4)

    private var arrowPaint: Paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = ARROW_STRENGTH
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        xArrowLength = w - 2 * arrowOffset
        yArrowLength = h - 2 * arrowOffset
        calcArrows()
    }

    private fun calcArrows() {
        var lines: ArrayList<Float> = ArrayList()

        // x Axis
        lines.add(arrowOffset)
        lines.add(arrowOffset + yArrowLength)
        lines.add(arrowOffset + xArrowLength)
        lines.add(arrowOffset + yArrowLength)

        // y Axis
        lines.add(arrowOffset)
        lines.add(arrowOffset)
        lines.add(arrowOffset)
        lines.add(arrowOffset + yArrowLength)

        val tips = getArrowTips()
        lines.addAll(tips)

        // Copy to Array
        lines.forEachIndexed { index, fl -> arrowLines[index] = fl }
    }

    private fun getArrowTips() : List<Float> {
        var lines: ArrayList<Float> = ArrayList()
        val tipLength = arrowOffset / 2f

        // x Tip
        var tipX = arrowOffset + xArrowLength
        var tipY = arrowOffset + yArrowLength
        lines.add(tipX - tipLength)
        lines.add(tipY - tipLength)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX - tipLength)
        lines.add(tipY + tipLength)

        // y Tip
        tipX = arrowOffset
        tipY = arrowOffset
        lines.add(tipX - tipLength)
        lines.add(tipY + tipLength)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX + tipLength)
        lines.add(tipY + tipLength)

        return lines
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.GRAY)
        drawArrows(canvas)
    }

    private fun drawArrows(canvas: Canvas) {
        canvas.drawLines(arrowLines, arrowPaint)
    }

}