package com.development_felber.dartapp.ui.screens.game.dialog.solo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.data.persistent.database.TestLegData
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.ui.screens.game.dialog.GameFinishedDialog
import com.development_felber.dartapp.ui.screens.game.dialog.SoloGameFinishedDialogViewModel
import com.development_felber.dartapp.ui.screens.historydetails.ServeDistributionChart
import com.development_felber.dartapp.ui.theme.DartAppTheme
import kotlinx.coroutines.delay

@Composable
fun SoloGameFinishedDialog(
    viewModel: SoloGameFinishedDialogViewModel,
    onPlayAgainClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    val showStats by viewModel.showStats.collectAsState()
    val leg by viewModel.leg.collectAsState()
    val last10GamesAverage by viewModel.last10GamesAverage.collectAsState()
    SoloGameFinishedDialogContent(showStats, leg ?: TestLegData.createRandomLeg(), last10GamesAverage,
        viewModel::onMoreDetailsClicked, onMenuClicked, onPlayAgainClicked)
}

@Composable
private fun SoloGameFinishedDialogContent(
    showStats: Boolean,
    leg: FinishedLeg,
    last10GamesAverage: Double,
    onMoreDetailsClicked: () -> Unit,
    onMenuClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit
) {
    GameFinishedDialog(
        title = "Leg finished!",
        showStatisticsContent = showStats,
        onMoreDetailsClicked = onMoreDetailsClicked,
        onMenuClicked = onMenuClicked,
        onPlayAgainClicked = onPlayAgainClicked
    ) {
        SoloStatsContent(
            leg = leg,
            last10GamesAverage = last10GamesAverage
        )
    }
}

@Composable
private fun SoloStatsContent(
    leg: FinishedLeg,
    last10GamesAverage: Double,
) {
    val animatedAverage = remember { Animatable(0f) }
    LaunchedEffect(key1 = leg) {
        delay(600)
        animatedAverage.animateTo(
            targetValue = leg.average.toFloat(),
            animationSpec = tween(
                durationMillis = 3000,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        ServeDistribution(leg = leg)

        AverageProgression(average = animatedAverage.value.toDouble(), last10GamesAverage = last10GamesAverage)

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SmallStatsCard(valueString = leg.dartCount.toString(), description = "Darts")

            SmallStatsCard(
                valueString = String.format("%.0f%%", 100.0 / leg.doubleAttempts),
                description = "Double rate"
            )

            SmallStatsCard(valueString = leg.checkout.toString(), description = "Checkout")
        }
    }
}


@Composable
private fun ServeDistribution(leg: FinishedLeg) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(0.93f)
            .height(170.dp),
        contentAlignment = Alignment.Center
    ) {
        ServeDistributionChart(leg = leg)
    }

}

@Preview(showBackground = true, widthDp = 300)
@Composable
private fun AverageProgressionPreview() {
    DartAppTheme() {
        AverageProgression(average = 61.0, last10GamesAverage = 40.1)
    }
}

@Composable
private fun AverageProgression(
    average: Double,
    last10GamesAverage: Double
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val last10GamesAverageString = if (last10GamesAverage.isNaN()) {
            "--"
        } else {
            String.format("%.1f", last10GamesAverage)
        }
        BigStatsCard(valueString = last10GamesAverageString, description = "Last 10 Games")

        var rotationDegrees = 0.0
        if (!last10GamesAverage.isNaN()) {
            val maxDegrees = 45.0
            if (average > last10GamesAverage) {
                rotationDegrees = (1 - last10GamesAverage / average) * maxDegrees * -1
            } else {
                rotationDegrees = (1 - average / last10GamesAverage) * maxDegrees
            }
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .aspectRatio(2.6f)
                .rotate(rotationDegrees.toFloat()),
            contentAlignment = Alignment.Center
        ) {
            Arrow()
        }

        BigStatsCard(valueString = String.format("%.1f", average), description = "Average")
    }
}

@Preview(showBackground = true, widthDp = 80, heightDp = 40)
@Composable
fun Arrow(strokeWidth: Float = 8f) {
    val color = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val arrowEnd = width - strokeWidth
        val arrowBaselineY = height / 2
        val arrowTipOffset = height / 2 - strokeWidth
        drawLine(
            color,
            Offset(strokeWidth, arrowBaselineY),
            Offset(arrowEnd, arrowBaselineY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Upper tip line
        drawLine(
            color,
            Offset(arrowEnd, arrowBaselineY),
            Offset(arrowEnd - arrowTipOffset, arrowBaselineY - arrowTipOffset),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Lower tip line
        drawLine(
            color,
            Offset(arrowEnd, arrowBaselineY),
            Offset(arrowEnd - arrowTipOffset, arrowBaselineY + arrowTipOffset),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun RowScope.BigStatsCard(
    valueString: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = valueString,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.SmallStatsCard(
    valueString: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = valueString,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Preview
@Composable
private fun SoloGameFinishedDialogPreview() {
    SoloGameFinishedDialogContent(
        showStats = true,
        leg = TestLegData.createRandomLeg(),
        last10GamesAverage = 42.0,
        onMoreDetailsClicked = {},
        onMenuClicked = {},
        onPlayAgainClicked = {}
    )
}