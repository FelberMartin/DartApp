package com.development_felber.dartapp.ui.screens.game.dialog.multi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.ui.screens.game.PlayerScore

data class CombinedScore(
    val legs1: Int,
    val legs2: Int,
    val legsToWin: Int,
    val sets1: Int,
    val sets2: Int,
    val setsToWin: Int,
) {
    fun toPlayerScore(isPlayer1: Boolean): PlayerScore {
        return if (isPlayer1) {
            PlayerScore(legs1, legsToWin, sets1, setsToWin)
        } else {
            PlayerScore(legs2, legsToWin, sets2, setsToWin)
        }
    }
}

@Composable
fun LegWonDialog(
    player1: Player,
    player2: Player,
    hasPlayer1WonLeg: Boolean,
    combinedScore: CombinedScore,
    onContinue: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /*Nothing*/ },
        title = {
            Text(
                text = if (hasPlayer1WonLeg) {
                    "${player1.name} won the leg!"
                } else {
                    "${player2.name} won the leg!"
                }
            )
        },
        text = {
            ScoreComparison(
                player1 = player1,
                player2 = player2,
                combinedScore = combinedScore
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
    player1: Player,
    player2: Player,
    combinedScore: CombinedScore,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerScore(
            playerName = player1.name,
            playerScore = combinedScore.toPlayerScore(true)
        )

        Text(
            text = "vs",
            style = MaterialTheme.typography.headlineLarge,
        )

        PlayerScore(
            playerName = player2.name,
            playerScore = combinedScore.toPlayerScore(false)
        )
    }
}

@Composable
private fun PlayerScore(
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
        Text(
            text = "${playerScore.setsWon}-${playerScore.legsWon}",
            style = MaterialTheme.typography.headlineLarge,
        )
    }

}