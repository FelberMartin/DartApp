package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.chartlibrary.compose.piechart.PieChartInternals
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.development_felber.dartapp.util.extensions.translated
import kotlin.math.PI
import kotlin.math.atan


@Composable
fun PieChart(
    dataSet: DataSet,
    animateDataSetChange: Boolean = true,
    selectionEnabled: Boolean = true,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig(),
    selectionGrowthModifier: Float = 1.15f,
    colorSequence: ColorSequence = ChartDefaults.colorSequence(),
    segmentDividerWidth: Dp = 6.dp,
) {
    if (dataSet.isEmpty()) {
        return
    }

    var pieChartInternals: PieChartInternals? = null

    Canvas(modifier = Modifier
        .aspectRatio(1f)
        .fillMaxSize()
        .graphicsLayer(alpha = 0.99f)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { tapOffset ->
                    println(pieChartInternals!!.getTouchedIndex(tapOffset))
                }
            )
        }
    ) {
        pieChartInternals = PieChartInternals(
            dataSet = dataSet,
            canvasSize = size,
            radiusToMaxRadiusRatio = 1 / selectionGrowthModifier
        )

        drawPieSegments(
            pieChartInternals = pieChartInternals!!,
            colorSequence = colorSequence
        )

        drawSegmentDividers(
            startPoints = pieChartInternals!!.startPoints,
            segmentDividerWidth = segmentDividerWidth.toPx()
        )
    }
}


private fun DrawScope.drawPieSegments(
    pieChartInternals: PieChartInternals,
    colorSequence: ColorSequence
) {
    for ((index, startAngle) in pieChartInternals.startAngles.withIndex()) {
        val sweepAngle = pieChartInternals.fractions[index] * 360f
//        val animatedSweepAngle = max(0f, shownMaxAngle - startAngle)
//        val shownSweepAngle = min(sweepAngle, animatedSweepAngle)

//        // let the selected arc protrude from the center
//        if (index == selectedIndex) {
//            canvas.save()
//            val dx = middlePoints[index].x * SELECTION_PROTRUDE
//            val dy = middlePoints[index].y * SELECTION_PROTRUDE
//            canvas.translate(dx, dy)
//        }

        drawArc(
            topLeft = pieChartInternals.topLeftOffset,
            useCenter = true,
            size = pieChartInternals.circleSize,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            color = colorSequence.getColor(index)
        )

//        if (index == selectedIndex)
//            canvas.restore()

    }
}

private fun DrawScope.drawSegmentDividers(
    startPoints: List<Offset>,
    segmentDividerWidth: Float
) {
    for (startPoint in startPoints) {
        drawLine(
            start = startPoint,
            end = center,
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


