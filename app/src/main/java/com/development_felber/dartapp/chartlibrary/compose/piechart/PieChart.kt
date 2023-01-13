package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.development_felber.dartapp.util.extensions.translated
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min

typealias TouchDetector = (Float, Float) -> Boolean

@Composable
fun PieChart(
    dataSet: DataSet,
    animateDataSetChange: Boolean = true,
    selectionEnabled: Boolean = true,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig(),
    colorSequence: ColorSequence = ChartDefaults.colorSequence(),
    segmentDividerWidth: Dp = 6.dp,
    firstStartAngle: Float = -90f
) {
    if (dataSet.isEmpty()) {
        return
    }

    val selectedIndexState = remember { mutableStateOf(-1) }
    var selectedIndex by selectedIndexState

    val maxSweepAngle = remember {
        Animatable(if (animateDataSetChange) -90f else 270f)
    }

    LaunchedEffect(key1 = dataSet) {
        selectedIndex = -1
        if (animateDataSetChange) {
            maxSweepAngle.snapTo(-90f)
        }
        maxSweepAngle.animateTo(
            targetValue = 270f,
            animationSpec = tween(1000)
        )
    }

    val touchDetectors = mutableListOf<TouchDetector>()
    var center by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        Modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        if (selectionEnabled) {
                            onTouch(
                                fromCenter = tapOffset.minus(center),
                                touchDetectors = touchDetectors,
                                selectedIndexState = selectedIndexState
                            )
                        }
                    }
                )
            }
            .onSizeChanged {
                center = Offset(it.width / 2f, it.height / 2f)
            }
    ) {
        var accumulatedFraction = 0f
        val dataSetSum = dataSet.sumOf { it.y }
        for ((index, dataPoint) in dataSet.withIndex()) {
            val fraction = (dataPoint.y / dataSetSum).toFloat()
            val startAngle = firstStartAngle + accumulatedFraction * 360f
            var sweepAngle = fraction * 360f
            if (startAngle + sweepAngle > maxSweepAngle.value) {
                sweepAngle = (maxSweepAngle.value - startAngle).coerceAtLeast(0f)
            }
            PieSegment(
                index = index,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                fullSweepAngle = fraction * 360f,
                selected = selectedIndex == index,
                normalToSelectedRadiusRatio = 0.95f,
                segmentDividerWidth = segmentDividerWidth,
                colorSequence = colorSequence,
                touchDetectors = touchDetectors
            )

            AnimatedSelectionInfoBox(
                selected = selectedIndex == index,
                dataPoint = dataPoint,
                selectionInfoBoxConfig = selectionInfoBoxConfig,
                angle = startAngle + fraction * 360f / 2f,
                center = center
            )

            accumulatedFraction += fraction
        }
    }
}

private fun onTouch(
    fromCenter: Offset,
    touchDetectors: List<TouchDetector>,
    selectedIndexState: MutableState<Int>
) {
    var selectedIndex by selectedIndexState

    var touchAngleRad = atan(fromCenter.y / fromCenter.x).toDouble()
    // atan only returns -PI/2 to PI/2
    if (fromCenter.x < 0)
        touchAngleRad += PI.toFloat()
    val touchAngle = touchAngleRad / (2 * Math.PI) * 360f

    val index = touchDetectors.indexOfFirst {
        it(touchAngle.toFloat(), fromCenter.getDistance())
    }
    if (index == selectedIndex) {
        selectedIndex = -1
    } else {
        selectedIndex = index
    }
}


@Composable
private fun PieSegment(
    index: Int,
    startAngle: Float,
    sweepAngle: Float,
    fullSweepAngle: Float,
    selected: Boolean,
    normalToSelectedRadiusRatio: Float,
    segmentDividerWidth: Dp,
    colorSequence: ColorSequence,
    touchDetectors: MutableList<TouchDetector>
) {
    val selectedTransition = updateTransition(targetState = selected, label = "selected")
    val percentOfSelectedRadius by selectedTransition.animateFloat(label = "radius") { selected ->
        if (selected) 1f else normalToSelectedRadiusRatio
    }
    val animatedSegmentDividerWidth by selectedTransition.animateDp(label = "segment_divider") {
            selected ->
        if (selected) segmentDividerWidth.minus(1.dp) else segmentDividerWidth
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer(alpha = 0.99f)   // For Alpha layer in BlendModes to work
    ) {
        val radiusSelected = size.minDimension / 2f
        val radius = radiusSelected * percentOfSelectedRadius
        val offset = radiusSelected - radius
        drawArc(
            topLeft = Offset(offset, offset),
            useCenter = true,
            size = Size(2f * radius, 2f * radius),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            color = colorSequence.getColor(index)
        )

        drawSegmentDivider(startAngle, animatedSegmentDividerWidth.toPx())
        drawSegmentDivider(startAngle + sweepAngle, animatedSegmentDividerWidth.toPx())

        touchDetectors.add { touchAngle, distance ->
            touchAngle >= startAngle && touchAngle <= startAngle + fullSweepAngle &&
                    distance <= radius
        }
    }
}

private fun DrawScope.drawSegmentDivider(
    angle: Float,
    segmentDividerWidth: Float
) {
    drawLine(
        start = center.translated(size.height, angle),
        end = center,
        blendMode = BlendMode.DstOut,
        strokeWidth = segmentDividerWidth,
        color = Color.Red,
        cap = StrokeCap.Round
    )
}


@Composable
private fun AnimatedSelectionInfoBox(
    selected: Boolean,
    dataPoint: DataPoint,
    selectionInfoBoxConfig: SelectionInfoBoxConfig,
    angle: Float,
    center: Offset
) {
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = if (selected) tween(1000) else tween(300)
    )

    val radius = min(center.x, center.y) * 0.6f
    val translated = center.translated(distance = radius, angleDegrees = angle)
    val offset = IntOffset(translated.x.toInt(), translated.y.toInt())

    SelectionInfoBox(
        title = dataPoint.x,
        subtitle = dataPoint.y.toString(),
        selectionInfoBoxConfig = selectionInfoBoxConfig,
        middleOffset = offset,
        modifier = Modifier
            .zIndex(1f)
            .alpha(alpha)
    )
}


@Preview(showBackground = true, widthDp = 300)
@Composable
private fun PieChartPreview() {
    DartAppTheme() {
        PieChart(
            dataSet = DataSet.pieChartTest(),
            animateDataSetChange = false
        )
    }
}


@Preview(showBackground = true, widthDp = 300)
@Composable
private fun AnimatedPieChartPreview() {
    DartAppTheme() {
        var dataSet by remember { mutableStateOf(DataSet.pieChartTest())}
        Column() {
            PieChart(dataSet)
            Button(onClick = {
                dataSet = DataSet.pieChartTest()
            }) {
                Text("Update")
            }
        }
    }
}
