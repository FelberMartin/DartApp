package com.development_felber.dartapp.chartlibrary.compose.piechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.development_felber.dartapp.chartlibrary.compose.DataSet
import com.development_felber.dartapp.util.extensions.translated
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.max

class PieChartInternals(
    val fractions: List<Float>,
    val accumulatedFractions: List<Float>,
    val radius: Float,
    val maxRadius: Float,
    val topLeftOffset: Offset,
    val circleSize: Size,
    val center: Offset,
    val startAngles: List<Float>,
    val startPoints: List<Offset>,
    val segmentMiddlePoints: List<Offset>
) {

    var selectedIndex: Int = -1

    fun getTouchedIndex(touch: Offset) : Int {
        val fromCenter = touch.minus(center)
        val directionIndex = getTouchDirectionIndex(fromCenter)

        if (fromCenter.getDistance() <= radius) {
            return directionIndex
        }

        val selected = selectedIndex == directionIndex
        if (selected && fromCenter.getDistance() <= maxRadius) {
            return directionIndex
        }
        return -1
    }

    private fun getTouchDirectionIndex(fromCenter: Offset): Int {
        var touchAngleRad = atan(fromCenter.y / fromCenter.x)

        // atan only returns -PI/2 to PI/2
        if (fromCenter.x < 0)
            touchAngleRad += PI.toFloat()

        val touchAngle = touchAngleRad / (2 * Math.PI) * 360f

        // finding the segment within which the touched angle lays
        for (index in startAngles.indices) {
            val from = startAngles[index]
            val to = startAngles[(index + 1) % startAngles.size]
            if (touchAngle in from..to || index == startAngles.size - 1) {
                return index
            }
        }
        return -1
    }

    companion object {
        operator fun invoke(
            dataSet: DataSet,
            canvasSize: Size,
            radiusToMaxRadiusRatio: Float,
            firstStartAngle: Float = -90f
        ) : PieChartInternals {
            val (fractions, accumulatedFractions) = getFractionsAndAccumulatedFractions(dataSet)

            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val maxRadius = canvasSize.minDimension / 2
            val radius = maxRadius * radiusToMaxRadiusRatio

            val (startAngles, startPoints, segmentMiddlePoints) = getAnglesAndPoints(
                fractions = fractions,
                firstStartAngle = firstStartAngle,
                center = center,
                radius = radius,
                maxRadius = maxRadius
            )

            return PieChartInternals(
                fractions = fractions,
                accumulatedFractions = accumulatedFractions,
                radius = radius,
                maxRadius = maxRadius,
                topLeftOffset = Offset(maxRadius - radius, maxRadius - radius),
                circleSize = Size(radius * 2f, radius * 2f),
                center = center,
                startAngles = startAngles,
                startPoints = startPoints,
                segmentMiddlePoints = segmentMiddlePoints
            )
        }

        private fun getFractionsAndAccumulatedFractions(
            dataSet: DataSet
        ) : Pair<List<Float>, List<Float>>{
            val fractions = mutableListOf<Float>()
            val accumulatedFractions = mutableListOf<Float>()
            val total = dataSet.sumOf { it.y }
            var accumulatedFraction = 0f
            for (dataPoint in dataSet) {
                val fraction = (dataPoint.y / total).toFloat()
                fractions.add(fraction)
                accumulatedFraction += fraction
                accumulatedFractions.add(accumulatedFraction)
            }
            return Pair(fractions, accumulatedFractions)
        }

        private fun getAnglesAndPoints(
            fractions: List<Float>,
            firstStartAngle: Float,
            center: Offset,
            radius: Float,
            maxRadius: Float,
        ) : Triple<List<Float>, List<Offset>, List<Offset>> {
            val startAngles = mutableListOf<Float>()
            val startPoints = mutableListOf<Offset>()
            val segmentMiddlePoints = mutableListOf<Offset>()
            var accumulatedFraction = 0f
            for (fraction in fractions) {
                val angle = firstStartAngle + accumulatedFraction * 360f
                startAngles.add(angle)
                startPoints.add(center.translated(maxRadius, angle))
                val halfWayAngle = fraction * 360f / 2f
                segmentMiddlePoints.add(center.translated(radius / 2f, halfWayAngle))
                accumulatedFraction += fraction
            }
            return Triple(startAngles, startPoints, segmentMiddlePoints)
        }

    }
}