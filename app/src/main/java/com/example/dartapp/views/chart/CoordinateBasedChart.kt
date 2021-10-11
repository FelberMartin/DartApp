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

// Number of lines needed to draw a single coordinate system arrow
private const val ARROW_LINES_COUNT = 2 * 3     // For each axis 3 lines

// How far the markers protrude from the coordinate system's arrows
private const val MARKER_SIZE = 10f
// Space between the Marker lines and the Labels
private const val MARKER_INTERN_SPACING = 15f

abstract class CoordinateBasedChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    // Markers on the x Axis
    private var xMarkers: ArrayList<Float> = arrayListOf()
    private var xMarkerTexts: ArrayList<String> = arrayListOf()
    private var xMarkerLabelsHeight = ARROW_TIP_SIZE

    // Markers on the y Axis
    private var yMarkers: ArrayList<Float> = arrayListOf()
    private var yMarkerTexts: ArrayList<String> = arrayListOf()
    private var yMarkerLabelsWidth = ARROW_TIP_SIZE

    // Automatically put some dynamic padding between the border of the coordinate system
    // and the outer values
    var horizontalAutoPadding = true
    var verticalAutoPadding = true

    // Extrema of the displayed values, not the extrema of the data points
    private var xMinValue = 0f
    private var xMaxValue = 0f
    private var yMinValue = 0f
    private var yMaxValue = 0f

    // Whether the axis should start at 0 or the min value of the data points
    var xStartAtZero = false
    var yStartAtZero = false

    // Rectangle including all the coordinates that can be displayed on this coordinate system
    private var coordRect = RectF()

    // mostly for debugging
    protected var drawCoordRect = false
    private val coordRectPaint = Paint().apply {
        color = Color.LTGRAY
    }

    // Offset of the coordinate system's arrows from the left of the view (x) and the bottom of
    // the view (y)
    private var arrowOffset: PointF = PointF(yMarkerLabelsWidth, xMarkerLabelsHeight)
    private var xArrowLength: Float = 300f
    private var yArrowLength: Float = 300f

    // The lines defining where the coordinate system's arrow are being drawn
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

        // Y values
        yMinValue = data.minOf { dp -> dp.y.toFloat() }
        yMaxValue = data.maxOf { dp -> dp.y.toFloat() }

        // X values
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

    /**
     * Changes the extrema values for x and y if the auto padding option is enabled
     */
    private fun handleAutoPadding() {
        if (horizontalAutoPadding) {
            val averageDist = (xMaxValue - xMinValue) / data.size
            xMinValue -= averageDist / 2
            xMaxValue += averageDist / 2
        }
        if (verticalAutoPadding) {
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
    }

    /**
     * Calculates the new lines for the coordinate system's arrows and stores them
     */
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

    /**
     * Produces the lines of the arrow tips
     */
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

    /**
     * Calculates the point in the coordinate system (NOT the pixel coordinates) of the data point
     * at the given index.
     * @param index Index of the DataPoint in the Chart's data set
     * @return The Point in the coordinate system the indexed data point lays
     */
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

    /**
     * This method calculates the x pixel position for a given x value in the chart's coordinate
     * system
     * @param x The x value in the Chart's coordinate system
     * @return The x pixel on the View's canvas
     */
    protected fun coordXToPixel(x: Float) : Float {
        val fraction = (x - xMinValue) / (xMaxValue - xMinValue)
        return coordRect.left + coordRect.width() * fraction
    }

    /**
     * This method calculates the y pixel position for a given y value in the chart's coordinate
     * system
     * @param y The y value in the Chart's coordinate system
     * @return The y pixel on the View's canvas
     */
    protected fun coordYToPixel(y: Float) : Float {
        val fraction = (y - yMinValue) / (yMaxValue - yMinValue)
        return coordRect.top + coordRect.height() * (1 - fraction)
    }

    /**
     * Updates all values related to the Chart's coordinate system.
     */
    private fun updateCoordSystem() {
        // only if the size has been initialized
        if (width == 0) return

        updateXMarkers()
        updateYMarkers()
        updateMarkerTexts()

        updateSpacings()
        updateArrows()

        coordRect = RectF(
            arrowOffset.x, ARROW_TIP_SIZE ,
            width - ARROW_TIP_SIZE, height - arrowOffset.y
        )

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

        /**
         * This method returns the Marker distance from a preset list at the given index
         */
        private fun getDistance(index: Int) : Float {
            if (index < markerDistances.size)
                return markerDistances[index]

            val i = index - markerDistances.size
            val zeros = 3 + i / bigMarkerSteps.size
            val multiplier = bigMarkerSteps[i % bigMarkerSteps.size]
            return (10.0.pow(zeros.toDouble()) * multiplier).toFloat()
        }

        /**
         * This method finds the best fitting distance for the markers depending on the displayed
         * distance and how many markers can be displayed.
         * @param maxCount How many markers should be displayed at a maximum.
         * @param fullDist How big is the distance from the displayed minimum to the maximum value.
         * @return The best fitting distance for the markers
         */
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

/**
 * Extension of FontMetrics.
 * Computes the offset between the baseline and the vertical center of the font (center between
 * top and bottom). This offset will be a negative value!
 * (See: https://stackoverflow.com/a/27631737/13366254)
 * @return The offset between this FontMetric's baseline and center.
 */
fun Paint.FontMetrics.baseLineCenterOffset() : Float {
    return (top + bottom) / 2
}

/**
 * Extension of FontMetrics.
 * Computes the total height this FontMetric can occupy.
 * (See: https://stackoverflow.com/a/27631737/13366254)
 * @return The height of the FontMetric.
 */
fun Paint.FontMetrics.height() : Float {
    return bottom - top
}

