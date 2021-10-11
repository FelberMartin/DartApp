package com.example.dartapp.views.chart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import androidx.annotation.AttrRes
import com.example.dartapp.R
import com.google.android.material.color.MaterialColors
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

fun View.getAttrColor(@AttrRes id: Int) : Int {
    return MaterialColors.getColor(this, id)
}

/**
 * How far the markers protrude from the coordinate system's arrows.
 */
private const val MARKER_SIZE = 10f

/**
 * Space between the Marker lines and the Labels.
 */
const val MARKER_INTERN_SPACING = 15f

class CoordMarkers(private val chart: CoordinateBasedChart, private val axis: Axis) {

    enum class Axis {
        X, Y
    }

    var maxWidth = 0f
    var maxHeight = 0f

    var minDistance = 150f

    private var texts = arrayListOf<String>()
    private var coords = arrayListOf<Float>()

    private val markerPaint = Paint().apply {
        isAntiAlias = true
        color = chart.getAttrColor(R.attr.colorOnBackground)
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        strokeWidth = ARROW_STRENGTH
        strokeCap = Paint.Cap.ROUND
        textAlign = when (axis) {
            Axis.X -> Paint.Align.CENTER
            Axis.Y -> Paint.Align.RIGHT
        }
    }

    init {
        minDistance = when (axis) {
            Axis.X -> 150f
            Axis.Y -> 250f
        }
    }

    /**
     * Updates all values related to the markers
     */
    fun update() {
        updateCoords()
        updateTexts()
        updateMaxima()
    }

    /**
     * Calculate the coordinates of the markers
     */
    private fun updateCoords() {
        coords.clear()

        // Setup depending on Axis orientation
        var chartSize = chart.width
        var minValue = chart.coordRect.left
        var maxValue = chart.coordRect.right
        var arrowLength = chart.xArrowLength
        if (axis == Axis.Y) {
            chartSize = chart.height
            minValue = chart.coordRect.top
            maxValue = chart.coordRect.bottom
            arrowLength = chart.yArrowLength
        }

        // Don't mark the areas near the arrow tips
        val tipPercentage = ARROW_TIP_SIZE / arrowLength
        val markablePortion = 1 - 3 * tipPercentage

        val fullDist = (maxValue - minValue) * markablePortion
        val maxCount = floor(chartSize / minDistance).toInt()

        val dist = findMarkerDistance(maxCount, fullDist)

        // Calculate the actual marker values depending on the returned marker distance
        val firstMultiplier = ceil(minValue / dist)
        var v = firstMultiplier * dist
        val maxMarkedValue = minValue + fullDist
        while (v <= maxMarkedValue) {
            coords.add(v)
            v += dist
        }
    }

    private fun updateTexts() {
        texts.clear()
        for (i in 0 until coords.size) {
            val text = when (axis) {
                Axis.X -> chart.data.xStringFrom(coords[i])
                Axis.Y -> DataPoint.format(coords[i])
            }
            texts.add(text)
        }
    }

    private fun updateMaxima() {
        maxWidth = texts.maxOf { t -> markerPaint.measureText(t) }
        maxHeight = markerPaint.fontMetrics.height()
    }

    /**
     * Draws the markers with respect to the CoordinateBasedChart given in the constructor.
     */
    fun draw(canvas: Canvas) {
        // X axis
        if (axis == Axis.X) {
            for (i in 0 until coords.size) {
                // Line
                val x = chart.coordXToPixel(coords[i])
                canvas.drawLine(x, chart.yArrowLength,
                    x, chart.yArrowLength + MARKER_SIZE,
                    markerPaint)

                // Label
                val text = texts[i]
                val y = chart.yArrowLength + MARKER_SIZE + MARKER_INTERN_SPACING
                canvas.drawText(text, x, y - markerPaint.fontMetrics.top, markerPaint)
            }
        }

        // Y axis
        else {
            for (i in 0 until coords.size) {
                // Line
                var y = chart.coordYToPixel(coords[i])
                canvas.drawLine(chart.arrowOffset.x, y,
                    chart.arrowOffset.x - MARKER_SIZE, y,
                    markerPaint)

                // Label
                val text = texts[i]
                y += - markerPaint.fontMetrics.baseLineCenterOffset()
                canvas.drawText(text, maxWidth, y, markerPaint)
            }
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