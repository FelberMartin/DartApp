package com.development_felber.dartapp.ui.screens.game.dialog.multi

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.game.Leg
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.ui.screens.game.PlayerScore
import com.development_felber.dartapp.ui.screens.game.PlayerUiState
import com.development_felber.dartapp.ui.screens.game.dialog.GameFinishedDialog
import com.development_felber.dartapp.ui.theme.DartAppTheme
import java.lang.Double.max
import java.lang.Double.min

data class GameOverallStatistics(
    val overallAverage: Double,
    val overallDoubleRate: Double,
) {
    companion object {
        fun fromLegs(legs: Collection<Leg>) = GameOverallStatistics(
            overallAverage = legs.map { it.getAverage() ?: 0.0 }.average(),
            overallDoubleRate = legs.filter { it.doubleAttempts > 0 }.map { 1.0 / it.doubleAttempts }.average()
        )
    }
}

@Composable
fun MultiplayerGameFinishedDialog(
    players: List<PlayerUiState>,
    onMenuClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit,
) {
    assert(players.size == 2) {
        "MultiplayerGameFinishedDialog only supports 2 players"
    }

    GameFinishedDialog(
        title = "Game finished!",
        onMoreDetailsClicked = { /* TODO: Not supported yet. */ },
        moreDetailsEnabled = false,
        onMenuClicked = onMenuClicked,
        onPlayAgainClicked = onPlayAgainClicked
    ) {

        MultiplayerStatsContent(
            players = players,
        )
    }
}

@Composable
fun MultiplayerStatsContent(
    players: List<PlayerUiState>,
) {
    val stats = players.map { it.gameOverallStatistics }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        ScoreComparison(
            player1 = players[0],
            player2 = players[1],
        )

        MultiplayerStatsRow(
            name = "Avg",
            leftValue = stats[0].overallAverage,
            rightValue = stats[1].overallAverage,
            stringFormat = "%.1f",
        )

        MultiplayerStatsRow(
            name = "Double Rate",
            leftValue = stats[0].overallDoubleRate * 100,
            rightValue = stats[1].overallDoubleRate * 100,
            stringFormat = "%.0f%%",
        )
    }
}

@Composable
private fun MultiplayerStatsRow(
    name: String,
    leftValue: Double,
    rightValue: Double,
    stringFormat: String,
) {
    val maxValue = remember { Animatable(0f) }
    LaunchedEffect(key1 = leftValue, key2 = rightValue) {
        val target = maxIgnoreNan(leftValue, rightValue).toFloat()
        if (target.isNaN()) {
            return@LaunchedEffect
        }
        maxValue.animateTo(
            targetValue = target,
            animationSpec = tween(
                durationMillis = 3000,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        val leftText = if (leftValue.isNaN()) {
            "-"
        } else {
            stringFormat.format(min(leftValue, maxValue.value.toDouble()))
        }
        Text(
            text = leftText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            )

        val rightText = if (rightValue.isNaN()) {
            "-"
        } else {
            stringFormat.format(min(rightValue, maxValue.value.toDouble()))
        }
        Text(
            text = rightText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            )
    }
}

private fun maxIgnoreNan(a: Double, b: Double) : Double {
    return if (a.isNaN()) {
        b
    } else if (b.isNaN()) {
        a
    } else {
        max(a, b)
    }
}

@Preview(showBackground = true)
@Composable
private fun MultiplayerGameFinishedDialogPreview() {
    DartAppTheme {
        MultiplayerGameFinishedDialog(
            players = listOf(
                PlayerUiState(
                    playerRole = PlayerRole.One,
                    name = "Player 1",
                    score = PlayerScore(1, 3, 0, 2),
                    average = 0.0,
                    dartCount = 0,
                    pointsLeft = 0,
                    last = 0,
                    gameOverallStatistics = GameOverallStatistics(50.0, Double.NaN),
                ),
                PlayerUiState(
                    playerRole = PlayerRole.Two,
                    name = "Player 2",
                    score = PlayerScore(2, 3, 1, 2),
                    average = 0.0,
                    dartCount = 0,
                    pointsLeft = 0,
                    last = 0,
                    gameOverallStatistics = GameOverallStatistics(61.2, .1),
                ),
            ),
            onMenuClicked = { },
            onPlayAgainClicked = { },
        )
    }
}
