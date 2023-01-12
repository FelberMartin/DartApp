package com.development_felber.dartapp.ui.screens.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.game.PlayerRole
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
        Row() {
            MultiplayerPlayerCounterCell(
                playerState = playerStateLeft,
                isActivePlayer = currentPlayerRole == playerStateLeft.playerRole,
            )

            Spacer(
                Modifier
                    .background(color = MaterialTheme.colorScheme.outline)
                    .width(1.dp)
                    .fillMaxHeight()
            )

            MultiplayerPlayerCounterCell(
                playerState = playerStateRight,
                isActivePlayer = currentPlayerRole == playerStateRight.playerRole,
            )
        }
    }
}

@Composable
private fun RowScope.MultiplayerPlayerCounterCell(
    playerState: PlayerUiState,
    isActivePlayer: Boolean,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        ActiveIndicator(isActivePlayer)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxHeight()
        ) {
            TopRow(
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
private fun ActiveIndicator(isActivePlayer: Boolean) {
    val color by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary.copy(
            alpha = if (isActivePlayer) 1f else 0f
        ),
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(color = color)
    )
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
        )
        CombinedMultiplayerPlayerCounter(
            playerStateLeft = playerStatsLeft,
            playerStateRight = playerStatsRight,
            currentPlayerRole = PlayerRole.One,
        )
    }
}