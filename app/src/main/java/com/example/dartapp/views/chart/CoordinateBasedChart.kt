package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors

private const val ARROW_STRENGTH = 5f
private const val ARROW_LINES_COUNT = 2 * 3     // For each axis 3 lines


abstract class CoordinateBasedChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    enum class XDistribution {
        EQUIDISTANT,
        SCALED_BY_VALUE
    }

    enum class XMarkerDistribution {
        EQUIDISTANT,
        WITH_VALUES
    }


    var xDistribution = XDistribution.EQUIDISTANT
    var xMarkerDistribution = XMarkerDistribution.WITH_VALUES
    var xEdgeAutoPadding = true
    var yEdgeAutoPadding = true

    private var xMinValue = 0f
    private var xMaxValue = 0f
    private var yMinValue = 0f
    private var yMaxValue = 0f

    var xStartAtZero = false
    var yStartAtZero = false

    protected var coordRect = RectF()
    protected var drawCoordRect = false
    private val coordRectPaint = Paint().apply {
        color = Color.LTGRAY
    }

    private val arrowOffset: Float = 20f
    private var xArrowLength: Float = 0f
    private var yArrowLength: Float = 0f

    private var arrowLines: FloatArray = FloatArray(ARROW_LINES_COUNT * 4)

    private var arrowPaint: Paint = Paint().apply {
        color = MaterialColors.getColor(this@CoordinateBasedChart, R.attr.colorOnBackground)
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = ARROW_STRENGTH
    }


    override fun dataChanged() {
        super.dataChanged()
        yMinValue = data.minOf { dp -> dp.y.toFloat() }
        yMaxValue = data.maxOf { dp -> dp.y.toFloat() }

        if (data.dataPointXType == DataSet.Type.STRING) {
            xMinValue = 0f
            xMaxValue = data.size.toFloat()
        } else {
            xMinValue = data.minOf { dp -> (dp.x as Number).toFloat() }
            xMaxValue = data.maxOf { dp -> (dp.x as Number).toFloat() }
        }

        if (xStartAtZero)
            xMinValue = 0f
        if (yStartAtZero)
            yMinValue = 0f

        handleAutoPadding()
    }

    private fun handleAutoPadding() {
        if (xEdgeAutoPadding) {
            val averageDist = (xMaxValue - xMinValue) / data.size
            xMinValue -= averageDist / 2
            xMaxValue += averageDist / 2
        }
        if (yEdgeAutoPadding) {
            val averageDist = (yMaxValue - yMinValue) / data.size
            yMinValue -= averageDist / 2
            yMaxValue += averageDist / 2
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        xArrowLength = w - 2 * arrowOffset
        yArrowLength = h - 2 * arrowOffset
        calcArrows()

        coordRect = RectF(
            arrowOffset, arrowOffset * 2,
            w - 2 * arrowOffset, h - arrowOffset
        )
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

    
    protected fun inCoordSystem(index: Int) : PointF {
        val coordX = getCoordX(index)
        val coordY = data[index].y.toFloat()
        return PointF(coordXToPixel(coordX), coordYToPixel(coordY))
    }

    private fun getCoordX(index: Int) : Float {
        if (data.dataPointXType == DataSet.Type.STRING)
            return index.toFloat()

        return (data[index].x as Number).toFloat()
    }

    protected fun coordXToPixel(x: Float) : Float {
        val fraction = (x - xMinValue) / (xMaxValue - xMinValue)
        return coordRect.left + coordRect.width() * fraction
    }
    protected fun coordYToPixel(y: Float) : Float {
        val fraction = (y - yMinValue) / (yMaxValue - yMinValue)
        return coordRect.top + coordRect.height() * (1 - fraction)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawCoordRect)
            canvas.drawRect(coordRect, coordRectPaint)

        drawArrows(canvas)
    }


    private fun drawArrows(canvas: Canvas) {
        canvas.drawLines(arrowLines, arrowPaint)
    }

}