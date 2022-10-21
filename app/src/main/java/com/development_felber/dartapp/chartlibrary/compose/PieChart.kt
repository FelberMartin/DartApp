package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun PieChart(
    dataSet: DataSet,
    animateDataSetChange: Boolean = true,
    selectionEnabled: Boolean = true,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig(),
    selectionGrowthModifier: Float = 1.15f,
    colorSequence: ColorSequence = ChartDefaults.colorSequence(),
    segmentDividerWidth: Dp = 8.dp,
) {
    if (dataSet.isEmpty()) {
        return
    }
    Canvas(modifier = Modifier
        .aspectRatio(1f)
        .fillMaxSize()
    ) {
        val radius = (size.minDimension / 2) / selectionGrowthModifier
        val startAngles = calculateStartAngles(dataSet)

        drawPieSegments(
            radius = radius,
            startAngles = startAngles,
            colorSequence = colorSequence
        )

        drawSegmentDividers(
            startAngles = startAngles,
            segmentDividerWidth = segmentDividerWidth.toPx()
        )
    }
}

private fun calculateStartAngles(
    dataSet: DataSet,
    startAngle: Float = -90f
) : List<Float> {
    val angles = mutableListOf<Float>()
    val totalSum = dataSet.sumOf { it.y }
    var accumulatedAngles = startAngle
    for (dataPoint in dataSet) {
        angles.add(accumulatedAngles)
        val fraction = dataPoint.y / totalSum
        val angle = 360f * fraction.toFloat()
        accumulatedAngles += angle
    }
    return angles
}

private fun DrawScope.drawPieSegments(
    radius: Float,
    startAngles: List<Float>,
    colorSequence: ColorSequence
) {
    for ((index, startAngle) in startAngles.withIndex()) {
        val nextStartAngle = startAngles[(index + 1) % startAngles.size]
        var sweepAngle = (nextStartAngle - startAngle) % 360f
        if (sweepAngle < 0f) {
            sweepAngle += 360f
        }
//        val animatedSweepAngle = max(0f, shownMaxAngle - startAngle)
//        val shownSweepAngle = min(sweepAngle, animatedSweepAngle)

//        // let the selected arc protrude from the center
//        if (index == selectedIndex) {
//            canvas.save()
//            val dx = middlePoints[index].x * SELECTION_PROTRUDE
//            val dy = middlePoints[index].y * SELECTION_PROTRUDE
//            canvas.translate(dx, dy)
//        }

        val offset = size.minDimension / 2 - radius
        drawArc(
            topLeft = Offset(offset, offset),
            useCenter = true,
            size = Size(2 * radius, 2 * radius),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            color = colorSequence.getColor(index)
        )

//        if (index == selectedIndex)
//            canvas.restore()

    }
}

private fun DrawScope.drawSegmentDividers(
    startAngles: List<Float>,
    segmentDividerWidth: Float
) {
    val maxRadius = size.minDimension / 2
    for (startAngle in startAngles) {
        val angleInRad = (startAngle / 360f * 2 * Math.PI).toFloat()
        val offsetFromCenter = Offset(cos(angleInRad) * maxRadius, sin(angleInRad) * maxRadius)
        drawLine(
            start = center,
            end = center.plus(offsetFromCenter),
            blendMode = BlendMode.DstOut,
            strokeWidth = segmentDividerWidth,
            color = Color.Red,
            cap = StrokeCap.Round
        )
    }
}


@Preview(showBackground = true, widthDp = 300)
@Composable
private fun PieChartPreview() {
    DartAppTheme() {
        PieChart(dataSet = DataSet.pieChartTest())
    }
}