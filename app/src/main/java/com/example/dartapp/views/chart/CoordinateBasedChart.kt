package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors
import kotlin.math.ceil
import kotlin.math.pow

private const val ARROW_STRENGTH = 8f
private const val ARROW_TIP_SIZE = 10f
private const val ARROW_LINES_COUNT = 2 * 3     // For each axis 3 lines

private const val MARKER_SIZE = 10f
private const val MARKER_INTERN_SPACING = 15f

abstract class CoordinateBasedChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    private var xMarkers: ArrayList<Float> = arrayListOf()
    private var xMarkerTexts: ArrayList<String> = arrayListOf()
    private var xMarkerLabelsHeight = ARROW_TIP_SIZE

    private var yMarkers: ArrayList<Float> = arrayListOf()
    private var yMarkerTexts: ArrayList<String> = arrayListOf()
    private var yMarkerLabelsWidth = ARROW_TIP_SIZE


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

    private var arrowOffset: PointF = PointF(yMarkerLabelsWidth, xMarkerLabelsHeight)
    private var xArrowLength: Float = 300f
    private var yArrowLength: Float = 300f

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

    private val markerPaint = Paint().apply {
        isAntiAlias = true
        color = MaterialColors.getColor(this@CoordinateBasedChart, R.attr.colorOnBackground)
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        strokeWidth = ARROW_STRENGTH
        strokeCap = Paint.Cap.ROUND
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
        updateCoordSystem()
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

        updateCoordSystem()
    }

    private fun updateArrows() {
        xArrowLength = width - arrowOffset.x - ARROW_STRENGTH / 2
        yArrowLength = height - arrowOffset.y - ARROW_STRENGTH / 2

        calcArrows()

        coordRect = RectF(
            arrowOffset.x, ARROW_TIP_SIZE ,
            width - ARROW_TIP_SIZE, height - arrowOffset.y
        )
    }

    private fun calcArrows() {
        var lines: ArrayList<Float> = ArrayList()

        // x Axis
        lines.add(arrowOffset.x)
        lines.add(yArrowLength)
        lines.add(arrowOffset.x + xArrowLength)
        lines.add(yArrowLength)

        // y Axis
        lines.add(arrowOffset.x)
        lines.add(ARROW_STRENGTH / 2)
        lines.add(arrowOffset.x)
        lines.add(yArrowLength)

        val tips = getArrowTips()
        lines.addAll(tips)

        // Copy to Array
        lines.forEachIndexed { index, fl -> arrowLines[index] = fl }
    }

    private fun getArrowTips() : List<Float> {
        var lines: ArrayList<Float> = ArrayList()

        // x Tip
        var tipX = arrowOffset.x + xArrowLength
        var tipY = yArrowLength
        lines.add(tipX - ARROW_TIP_SIZE)
        lines.add(tipY - ARROW_TIP_SIZE)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX - ARROW_TIP_SIZE)
        lines.add(tipY + ARROW_TIP_SIZE)

        // y Tip
        tipX = arrowOffset.x
        tipY = ARROW_STRENGTH / 2
        lines.add(tipX - ARROW_TIP_SIZE)
        lines.add(tipY + ARROW_TIP_SIZE)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX + ARROW_TIP_SIZE)
        lines.add(tipY + ARROW_TIP_SIZE)

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

    private fun updateCoordSystem() {
        // only if the size has been initialized
        if (width == 0) return

        updateXMarkers()
        updateYMarkers()
        updateMarkerTexts()

        updateSpacings()
        updateArrows()

        invalidate()
    }

    private fun updateMarkerTexts() {
        // X Markers
        xMarkerTexts = ArrayList()
        for (i in 0 until xMarkers.size) {
            val text = data.xStringFrom(xMarkers[i])
            xMarkerTexts.add(text)
        }

        // Y Markers
        yMarkerTexts = ArrayList()
        for (i in 0 until yMarkers.size) {
            val text = DataPoint.format(yMarkers[i])
            yMarkerTexts.add(text)
        }
    }

    private fun updateSpacings() {
        xMarkerLabelsHeight = markerPaint.fontMetrics.height()
        yMarkerLabelsWidth = yMarkerTexts.maxOf { t -> markerPaint.measureText(t) }

        arrowOffset.x = yMarkerLabelsWidth + MARKER_INTERN_SPACING
        arrowOffset.y = xMarkerLabelsHeight + MARKER_INTERN_SPACING
    }

    private fun updateXMarkers() {
        xMarkers = ArrayList()
        val maxCount = ceil(width / 150f).toInt()
        val fullDist = xMaxValue - xMinValue
        val dist = findMarkerDistance(maxCount, fullDist)
        val firstMultiplier = ceil(xMinValue / dist)
        var x = firstMultiplier * dist
        val tipPercentage = ARROW_TIP_SIZE / xArrowLength
        val maxMarkedValue = xMaxValue - 3 * tipPercentage * fullDist
        while (x <= maxMarkedValue) {
            xMarkers.add(x)
            x += dist
        }
    }

    private fun updateYMarkers() {
        yMarkers = ArrayList()
        val maxCount = ceil(height / 250f).toInt()
        val fullDist = yMaxValue - yMinValue
        val dist = findMarkerDistance(maxCount, fullDist)
        val firstMultiplier = ceil(yMinValue / dist)
        var y = firstMultiplier * dist
        val tipPercentage = ARROW_TIP_SIZE / yArrowLength
        val maxMarkedValue = yMaxValue - 3 * tipPercentage * fullDist
        while (y <= maxMarkedValue) {
            yMarkers.add(y)
            y += dist
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawCoordRect)
            canvas.drawRect(coordRect, coordRectPaint)

        drawMarkers(canvas)
        drawArrows(canvas)
    }


    private fun drawArrows(canvas: Canvas) {
        canvas.drawLines(arrowLines, arrowPaint)
    }

    private fun drawMarkers(canvas: Canvas) {
        // X Markers
        markerPaint.textAlign = Paint.Align.CENTER
        for (i in 0 until xMarkers.size) {
            // Line
            val x = coordXToPixel(xMarkers[i])
            canvas.drawLine(x, yArrowLength, x, yArrowLength + MARKER_SIZE, markerPaint)

            // Label
            val text = xMarkerTexts[i]
            val y = yArrowLength + MARKER_SIZE + MARKER_INTERN_SPACING
            canvas.drawText(text, x, y - markerPaint.fontMetrics.top, markerPaint)
        }

        // Y Markers
        markerPaint.textAlign = Paint.Align.RIGHT
        for (i in 0 until yMarkers.size) {
            // Line
            var y = coordYToPixel(yMarkers[i])
            canvas.drawLine(arrowOffset.x, y, arrowOffset.x - MARKER_SIZE, y, markerPaint)

            // Label
            val text = yMarkerTexts[i]
            y += - markerPaint.fontMetrics.baseLineCenterOffset()
            canvas.drawText(text, yMarkerLabelsWidth, y, markerPaint)
        }
    }


    /**
     * For finding the right marker distances
     */
    companion object {
        private val markerDistances = listOf<Float>(0.1f, 0.2f, 0.5f, 1f, 2f, 5f, 10f, 20f,
            25f, 40f, 50f, 100f, 125f, 150f, 200f, 500f)

        private val bigMarkerSteps = listOf(1f, 2f, 5f)

        fun getDistance(index: Int) : Float {
            if (index < markerDistances.size)
                return markerDistances[index]

            val i = index - markerDistances.size
            val zeros = 3 + i / bigMarkerSteps.size
            val multiplier = bigMarkerSteps[i % bigMarkerSteps.size]
            return (10.0.pow(zeros.toDouble()) * multiplier).toFloat()
        }

        fun findMarkerDistance(maxCount: Int, fullDist: Float) : Float {
            var index = 0
            while (true) {
                val dist = getDistance(index)
                if (fullDist / dist <= maxCount)
                    return dist

                index++
            }
        }


    }


}

fun Paint.FontMetrics.baseLineCenterOffset() : Float {
    return (top + bottom) / 2
}

fun Paint.FontMetrics.height() : Float {
    return bottom - top
}

