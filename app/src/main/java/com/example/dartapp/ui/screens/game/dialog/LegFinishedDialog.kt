package com.example.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.TestLegData
import com.example.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.historydetails.ServeDistributionChart
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.extensions.observeAsStateNonOptional


@Composable
fun LegFinishedDialog(
    viewModel: LegFinishedDialogViewModel,
    onPlayAgainClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Leg finished!")
        },
        text = {
            if (viewModel.showStats.observeAsStateNonOptional().value) {
                StatisticsSection(viewModel = viewModel)
            } else {
                Text("Would you like to play again?")
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onMenuClicked) {
                    Text("Back to Menu")
                }

                Spacer(Modifier.width(12.dp))

                Button(onClick = onPlayAgainClicked) {
                    Text("Play again")
                }
            }

        }
    )
}

@Composable
private fun StatisticsSection(viewModel: LegFinishedDialogViewModel) {
    val leg = viewModel.leg
    MyCard(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            if (viewModel.showServeDistribution) {
                ServeDistribution(leg = leg)
            }
            if (viewModel.showAverage) {
                val last10GamesAverage by viewModel.last10GamesAverage.observeAsStateNonOptional()
                AverageProgression(average = leg.average, last10GamesAverage = last10GamesAverage)
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.showDartCount) {
                    SmallStatsCard(valueString = leg.dartCount.toString(), description = "Darts")
                }
                if (viewModel.showDoubleRate) {
                    SmallStatsCard(
                        valueString = String.format("%.0f%%", 100.0 / leg.doubleAttempts),
                        description = "Double rate"
                    )
                }
                if (viewModel.showCheckout) {
                    SmallStatsCard(valueString = leg.checkout.toString(), description = "Checkout")
                }
            }

            if (viewModel.showDetailsLinkButton) {
                MoreDetailsButton(onClick = viewModel::onMoreDetailsClicked)
            }
        }
    }
}



@Composable
private fun ServeDistribution(leg: Leg) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(1.0f)
            .height(170.dp)
            .scale(0.9f),
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
        BigStatsCard(valueString = String.format("%.1f", last10GamesAverage), description = "Last 10 Games")

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

@Composable
private fun MoreDetailsButton(onClick: (() -> Unit)) {
    OutlinedButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.StackedLineChart,
            contentDescription = null
        )
        Text(
            text = "more Details",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Preview
@Composable
private fun LegFinishedDialogPreview() {
    val viewModel = LegFinishedDialogViewModel(
        navigationManager = NavigationManager(),
        leg =  TestLegData.createExampleLegs().first(),
        databaseDao = FakeLegDatabaseDao(),
        settingsRepository = SettingsRepository(InMemoryKeyValueStorage())
    )

    LegFinishedDialog(
        onMenuClicked = {},
        onPlayAgainClicked = {},
        viewModel = viewModel
    )
}