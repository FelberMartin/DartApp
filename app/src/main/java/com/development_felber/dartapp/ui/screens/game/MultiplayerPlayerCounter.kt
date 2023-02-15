package com.development_felber.dartapp.ui.screens.game

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.ui.screens.game.dialog.multi.GameOverallStatistics
import com.development_felber.dartapp.ui.shared.MyCard
import com.development_felber.dartapp.ui.theme.DartAppTheme


data class PlayerScore(
    val legsWon: Int,
    val legsToWin: Int,
    val setsWon: Int,
    val setsToWin: Int,
)

@Composable
fun CombinedMultiplayerPlayerCounter(
    playerStateLeft: PlayerUiState,
    playerStateRight: PlayerUiState,
    currentPlayerRole: PlayerRole,
) {
    MyCard(
        modifier = Modifier.height(180.dp)
    ) {
        Column() {
            RoleIndicator(playerRole = currentPlayerRole)

            Row() {
                MultiplayerPlayerCounterCell(
                    playerState = playerStateLeft,
                )

                Spacer(
                    Modifier
                        .background(color = MaterialTheme.colorScheme.outline)
                        .width(1.dp)
                        .fillMaxHeight()
                )

                MultiplayerPlayerCounterCell(
                    playerState = playerStateRight,
                )
            }
        }

    }
}

@Composable
private fun RoleIndicator(
    playerRole: PlayerRole,
) {
    val alignment by animateHorizontalAlignmentAsState(
        targetBiasValue = when (playerRole) {
            PlayerRole.One -> -1f
            PlayerRole.Two -> 1f
        },
        animationSpec = tween(400)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(8.dp)
                .background(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float,
    animationSpec: AnimationSpec<Float> = tween(300),
): State<BiasAlignment.Horizontal> {
    val bias by animateFloatAsState(targetBiasValue, animationSpec)
    return derivedStateOf { BiasAlignment.Horizontal(bias) }
}

@Composable
private fun RowScope.MultiplayerPlayerCounterCell(
    playerState: PlayerUiState,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight()
        ) {
            com.development_felber.dartapp.ui.screens.game.TopRow(
                name = playerState.name,
                playerScore = playerState.score
            )

            Text(
                text = playerState.pointsLeft.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
            )

            StatsRow(
                last = playerState.last,
                average = playerState.average,
            )
        }
    }
}

@Composable
private fun TopRow(
    name: String,
    playerScore: PlayerScore,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        ScoreRepresentation(playerScore = playerScore)
    }
}

@Composable
private fun ScoreRepresentation(
    playerScore: PlayerScore
) {
    // TODO: Do this with dots and dashes? (See figma)
    Text(
        text = "${playerScore.setsWon}-${playerScore.legsWon}",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun StatsRow(
    last: Int?,
    average: Double?,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerGameStatistic(
            name = "Last:",
            valueString = last?.toString() ?: "-",
        )
        PlayerGameStatistic(
            name = "Ø",
            valueString = average?.let { String.format("%.1f", it) } ?: "-",
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CombinedMultiplayerPlayerStatsPreview() {
    DartAppTheme() {
        val playerStatsLeft = PlayerUiState(
            name = "Player 1",
            playerRole = PlayerRole.One,
            score = PlayerScore(
                legsWon = 1,
                legsToWin = 3,
                setsWon = 0,
                setsToWin = 2,
            ),
            pointsLeft = 120,
            last = 66,
            average = 20.0,
            dartCount = 12,
            gameOverallStatistics = GameOverallStatistics(0.0, 0.0),
        )
        val playerStatsRight = PlayerUiState(
            name = "Player 2",
            playerRole = PlayerRole.Two,
            score = PlayerScore(
                legsWon = 2,
                legsToWin = 3,
                setsWon = 1,
                setsToWin = 2,
            ),
            pointsLeft = 420,
            last = 20,
            average = 54.2,
            dartCount = 11,
            gameOverallStatistics = GameOverallStatistics(0.0, 0.0),
            )
        CombinedMultiplayerPlayerCounter(
            playerStateLeft = playerStatsLeft,
            playerStateRight = playerStatsRight,
            currentPlayerRole = PlayerRole.One,
        )
    }
}