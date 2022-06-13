package com.example.dartapp.views.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import com.example.dartapp.R
import com.example.dartapp.util.replaceNaN
import com.example.dartapp.views.chart.util.CoordMarkers
import com.example.dartapp.views.chart.util.DataSet
import com.example.dartapp.views.chart.util.getAttrColor
import com.google.android.material.color.MaterialColors
import kotlin.math.max

const val ARROW_STRENGTH = 8f
const val ARROW_TIP_SIZE = 10f

/**
 * Number of lines needed to draw a single coordinate system arrow
 */
private const val ARROW_LINES_COUNT = 2 * 3     // For each axis 3 lines


abstract class CoordinateBasedChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Chart(context, attrs, defStyleAttr) {

    // Automatically put some dynamic padding between the border of the coordinate system
    // and the outer values
    var horizontalAutoPadding = true
    var verticalAutoPadding = true
    var topAutoPadding = true

    // Whether the axis should start at 0 or the min value of the data points
    var xStartAtZero = false
    var yStartAtZero = false

    var showXAxisArrow = true
    var showXAxisMarkers = true

    /**
     * Rectangle including all the coordinates that can be displayed on this coordinate system.
     * Extrema of the displayed values, not the extrema of the data points.
     * Caution: .top describes the minimum value and .bottom the maximum value, so inverted to
     *          coordinate system!!
     */
    var coordRect = RectF()

    val xMarkers = CoordMarkers(this, CoordMarkers.Axis.X)
    val yMarkers = CoordMarkers(this, CoordMarkers.Axis.Y)

    // Rectangle representing the coordinate system's occupied area on the canvas
    protected var coordPixelRect = RectF()

    // mostly for debugging
    protected var drawCoordRect = false
    private val coordRectPaint = Paint().apply {
        color = Color.LTGRAY
    }


    // Offset of the coordinate system's arrows from the left of the view (x) and the bottom of
    // the view (y)
    var arrowOffset = PointF()
    var xArrowLength: Float = 300f
    var yArrowLength: Float = 300f

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

    var showHorizontalGrid = true
    var showVerticalGrid = true
    private var horizontalGridLines = FloatArray(0)
    private var verticalGridLines = FloatArray(0)
    private val gridPaint = Paint().apply {
        color = getAttrColor(R.attr.colorBackgroundFloating)
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 2f
    }

    override fun reload() {
        super.reload()

        if (data.isEmpty()) {
            coordRect = RectF()
        } else {
            // Y values
            coordRect.top = data.minOf { dp -> dp.y.toFloat().replaceNaN(0f) }
            coordRect.bottom = data.maxOf { dp -> dp.y.toFloat().replaceNaN(0f) }

            // X values
            if (data.dataPointXType == DataSet.Type.STRING) {
                coordRect.left = 0f
                coordRect.right = data.size.toFloat() - 1
            } else {
                coordRect.left = data.minOf { dp -> (dp.x as Number).toFloat() }
                coordRect.right = data.maxOf { dp -> (dp.x as Number).toFloat() }
            }

            if (xStartAtZero)
                coordRect.left = 0f
            if (yStartAtZero)
                coordRect.top = 0f
        }

        handleAutoPadding()
        updateCoordSystem()

        Log.d("Coords", "$coordRect")
    }

    /**
     * Changes the extrema values for x and y if the auto padding option is enabled
     */
    private fun handleAutoPadding() {
        if (coordRect.height() == 0f || coordRect.width() == 0f) {
            handleConstantData()
        }

        if (data.size < 2) return

        if (horizontalAutoPadding) {
            val averageDist = coordRect.width() / (data.size - 1)
            coordRect.left -= averageDist / 2
            coordRect.right += averageDist / 2
        }
        if (verticalAutoPadding) {
            val averageDist = coordRect.height() / (data.size - 1)
            coordRect.top -= averageDist / 2
            coordRect.bottom += averageDist / 2
        } else if (topAutoPadding) {
            coordRect.bottom += coordRect.height() * 0.1f
        }
    }

    private fun handleConstantData() {
        if (coordRect.width() == 0f)
            coordRect.inset(-1f, 0f)
        if (coordRect.height() == 0f)
            coordRect.inset(0f, -1f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updateCoordSystem()
    }

    private fun updateArrows() {
        xArrowLength = width - arrowOffset.x
        yArrowLength = height - arrowOffset.y

        calcArrows()
    }

    /**
     * Calculates the new lines for the coordinate system's arrows and stores them
     */
    private fun calcArrows() {
        val lines: ArrayList<Float> = ArrayList()

        // x Axis
        lines.add(arrowOffset.x)
        lines.add(yArrowLength)
        lines.add(arrowOffset.x + xArrowLength)
        lines.add(yArrowLength)

        // y Axis
        lines.add(arrowOffset.x)
        lines.add(ARROW_STRENGTH / 2)
        lines.add(arrowOffset.x)
        lines.add(yArrowLength - ARROW_STRENGTH / 2)

        val tips = getArrowTips()
        lines.addAll(tips)

        // Copy to Array
        arrowLines = FloatArray(lines.size)
        lines.forEachIndexed { index, fl -> arrowLines[index] = fl }
    }

    /**
     * Produces the lines of the arrow tips
     */
    private fun getArrowTips() : List<Float> {
        val lines: ArrayList<Float> = ArrayList()

        // y Tip
        var tipX = arrowOffset.x
        var tipY = ARROW_STRENGTH / 2
        lines.add(tipX - ARROW_TIP_SIZE)
        lines.add(tipY + ARROW_TIP_SIZE)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX + ARROW_TIP_SIZE)
        lines.add(tipY + ARROW_TIP_SIZE)

        if (!showXAxisArrow) return lines

        // x Tip
        tipX = arrowOffset.x + xArrowLength - ARROW_STRENGTH / 2
        tipY = yArrowLength
        lines.add(tipX - ARROW_TIP_SIZE)
        lines.add(tipY - ARROW_TIP_SIZE)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX)
        lines.add(tipY)
        lines.add(tipX - ARROW_TIP_SIZE)
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
    fun coordXToPixel(x: Float) : Float {
        var fraction = 0f
        if (coordRect.width() > 0) {
            fraction = (x - coordRect.left) / coordRect.width()
        }
        return coordPixelRect.left + coordPixelRect.width() * fraction
    }

    /**
     * This method calculates the y pixel position for a given y value in the chart's coordinate
     * system
     * @param y The y value in the Chart's coordinate system
     * @return The y pixel on the View's canvas
     */
    fun coordYToPixel(y: Float) : Float {
        var fraction = 0f
        if (coordRect.height() > 0) {
            fraction = (y - coordRect.top) / coordRect.height()
        }
        return coordPixelRect.top + coordPixelRect.height() * (1 - fraction)
    }

    /**
     * Updates all values related to the Chart's coordinate system.
     */
    private fun updateCoordSystem() {
        // only if the size has been initialized
        if (width == 0) return

        xMarkers.update()
        yMarkers.update()

        updateSpacings()
        updateArrows()

        coordPixelRect = RectF(
            arrowOffset.x, ARROW_TIP_SIZE ,
            width - ARROW_TIP_SIZE, height - arrowOffset.y
        )

        updateGrid()

        invalidate()
    }

    private fun updateSpacings() {
        arrowOffset.x = yMarkers.requiredWidth
        arrowOffset.y = xMarkers.requiredHeight

        val plainXAxis = !showXAxisArrow && !showXAxisMarkers
        if (plainXAxis) {
            // if there are no markers make sure that the y Markers fit into the view if they are
            // on the really bottom
            val lowestMarker = coordYToPixel(yMarkers.coords[0])
            val deepestPoint = lowestMarker + yMarkers.maxTextHeight / 2
            val lowestY = coordYToPixel(coordRect.top)
            val overShoot = deepestPoint - lowestY
            arrowOffset.y = max(overShoot, ARROW_STRENGTH / 2)
        }
    }

    private fun updateGrid() {
        // Vertical
        val gridLines = arrayListOf<Float>()
        val yFrom = coordPixelRect.top
        val yTo = coordPixelRect.bottom
        for (xCoord in xMarkers.coords) {
            val x = coordXToPixel(xCoord)
            gridLines.add(x)
            gridLines.add(yFrom)
            gridLines.add(x)
            gridLines.add(yTo)
        }
        verticalGridLines = gridLines.toFloatArray()

        // Horizontal
        gridLines.clear()
        val xFrom = coordPixelRect.left
        val xTo = coordPixelRect.right
        for (yCoord in yMarkers.coords) {
            val y = coordYToPixel(yCoord)
            gridLines.add(xFrom)
            gridLines.add(y)
            gridLines.add(xTo)
            gridLines.add(y)
        }
        horizontalGridLines = gridLines.toFloatArray()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawCoordRect)
            canvas.drawRect(coordPixelRect, coordRectPaint)

        drawMarkers(canvas)
        drawGrid(canvas)
        drawArrows(canvas)
    }

    protected fun drawMarkers(canvas: Canvas) {
        if (showXAxisMarkers)
            xMarkers.draw(canvas)
        yMarkers.draw(canvas)
    }

    protected fun drawGrid(canvas: Canvas) {
        if (showHorizontalGrid)
            canvas.drawLines(horizontalGridLines, gridPaint)
        if (showVerticalGrid)
            canvas.drawLines(verticalGridLines, gridPaint)
    }

    protected fun drawArrows(canvas: Canvas) {
        canvas.drawLines(arrowLines, arrowPaint)
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

