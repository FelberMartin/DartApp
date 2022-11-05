package com.development_felber.dartapp.ui.screens.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

data class Score(
    val legs: Int,
    val legsToWin: Int,
    val sets: Int,
    val setsToWin: Int,
)

data class PlayerStats(
    val name: String,
    val score: Score,
    val pointsLeft: Int,
    val last: Int,
    val average: Double,
)

enum class GameStatus {
    LeftPlayersTurn,
    RightPlayersTurn,
    Other
}

@Composable
fun CombinedMultiplayerPlayerStats(
    playerStatsLeft: PlayerStats,
    playerStatsRight: PlayerStats,
    gameStatus: GameStatus,
) {
    Card(
        modifier = Modifier.height(180.dp)
    ) {
        Row() {
            MultiplayerPlayerStatsCell(
                playerStats = playerStatsLeft,
                isActivePlayer = gameStatus == GameStatus.LeftPlayersTurn,
            )

            Spacer(
                Modifier
                    .background(color = MaterialTheme.colorScheme.outline)
                    .width(1.dp)
                    .fillMaxHeight()
            )

            MultiplayerPlayerStatsCell(
                playerStats = playerStatsRight,
                isActivePlayer = gameStatus == GameStatus.RightPlayersTurn,
            )
        }
    }
}

@Composable
private fun RowScope.MultiplayerPlayerStatsCell(
    playerStats: PlayerStats,
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
                name = playerStats.name,
                score = playerStats.score
            )

            Text(
                text = playerStats.pointsLeft.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
            )

            StatsRow(
                last = playerStats.last,
                average = playerStats.average,
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
    score: Score,
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
        ScoreRepresentation(score = score)
    }
}

@Composable
private fun ScoreRepresentation(
    score: Score
) {
    // TODO: Do this with dots and dashes? (See figma)
    Text(
        text = "${score.sets}-${score.legs}",
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun StatsRow(
    last: Int,
    average: Double,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerGameStatistic(
            name = "Last:",
            valueString = last.toString()
        )
        PlayerGameStatistic(
            name = "Ø",
            valueString = String.format("%.1f", average)
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CombinedMultiplayerPlayerStatsPreview() {
    DartAppTheme() {
        val playerStatsLeft = PlayerStats(
            name = "Player 1",
            score = Score(
                legs = 1,
                legsToWin = 3,
                sets = 0,
                setsToWin = 2,
            ),
            pointsLeft = 120,
            last = 66,
            average = 20.0,
        )
        val playerStatsRight = PlayerStats(
            name = "Player 2",
            score = Score(
                legs = 2,
                legsToWin = 3,
                sets = 1,
                setsToWin = 2,
            ),
            pointsLeft = 420,
            last = 20,
            average = 54.2,
        )
        CombinedMultiplayerPlayerStats(
            playerStatsLeft = playerStatsLeft,
            playerStatsRight = playerStatsRight,
            gameStatus = GameStatus.LeftPlayersTurn
        )
    }
}