package com.development_felber.dartapp.ui.screens.game.dialog.multi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.ui.screens.game.PlayerScore
import com.development_felber.dartapp.ui.screens.game.PlayerUiState
import com.development_felber.dartapp.ui.theme.DartAppTheme



@Composable
fun LegOrSetWonDialog(
    setOver: Boolean,
    players: List<PlayerUiState>,
    playerWon: PlayerRole,
    onContinue: () -> Unit,
) {
    assert(players.size == 2) { "LegOrLegWonDialog only supports 2 players" }

    AlertDialog(
        onDismissRequest = { /*Nothing*/ },
        title = {
            val winner = players.first { it.playerRole == playerWon }
            Text(
                text = "${winner.name} won the ${if (setOver) "set" else "leg"}!"
            )
        },
        text = {
            ScoreComparison(
                player1 = players[0],
                player2 = players[1],
            )
        },
        confirmButton = {
            Button(onClick = onContinue) {
                Text(text = "Continue")
            }
        },

    )
}

@Composable
fun ScoreComparison(
    player1: PlayerUiState,
    player2: PlayerUiState,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerScoreDisplay(
            playerName = player1.name,
            playerScore = player1.score,
        )

        Text(
            text = "vs",
            style = MaterialTheme.typography.headlineLarge,
        )

        PlayerScoreDisplay(
            playerName = player2.name,
            playerScore = player2.score
        )
    }
}

@Composable
fun PlayerScoreDisplay(
    playerName: String,
    playerScore: PlayerScore,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = playerName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        LegsInSetIndicator(score = playerScore)
        Text(
            text = "${playerScore.legsWon}-${playerScore.setsWon}",
            style = MaterialTheme.typography.headlineLarge,
        )
        SetsInGameIndicator(score = playerScore)
    }
}

@Composable
private fun LegsInSetIndicator(score: PlayerScore) {
    if (score.legsToWin == 1 || score.setsToWin > 5) {
        return
    }
    Row() {
        for (i in 0 until score.legsToWin) {
            val color = if (i < score.legsWon) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .padding(4.dp)
                    .background(color = color, shape = MaterialTheme.shapes.extraLarge)
            )
        }
    }
}

@Composable
private fun SetsInGameIndicator(score: PlayerScore) {
    if (score.setsToWin == 1 || score.setsToWin > 3) {
        return
    }
    Row() {
        for (i in 0 until score.setsToWin) {
            val color = if (i < score.setsWon) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
            Box(
                modifier = Modifier
                    .size(height = 20.dp, width = 32.dp)
                    .padding(4.dp)
                    .background(color = color, shape = MaterialTheme.shapes.extraLarge)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun LegWonDialogPreview() {
    DartAppTheme() {
        LegOrSetWonDialog(
            setOver = false,
            players = listOf(
                PlayerUiState(
                    playerRole = PlayerRole.One,
                    name = "Player 1",
                    score = PlayerScore(1, 3, 0, 2),
                    average = 0.0,
                    dartCount = 0,
                    pointsLeft = 0,
                    last = 0,
                    gameOverallStatistics = GameOverallStatistics(0.0, 0.0),
                    ),
                PlayerUiState(
                    playerRole = PlayerRole.Two,
                    name = "Player 2",
                    score = PlayerScore(2, 3, 1, 2),
                    average = 0.0,
                    dartCount = 0,
                    pointsLeft = 0,
                    last = 0,
                    gameOverallStatistics = GameOverallStatistics(0.0, 0.0),
                ),
            ),
            playerWon = PlayerRole.One,
            onContinue = {}
        )
    }
}