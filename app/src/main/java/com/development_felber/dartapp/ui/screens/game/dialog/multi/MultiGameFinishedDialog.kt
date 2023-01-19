package com.development_felber.dartapp.ui.screens.game.dialog.multi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGame
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.ui.screens.game.PlayerScore
import com.development_felber.dartapp.ui.screens.game.PlayerUiState
import com.development_felber.dartapp.ui.screens.game.dialog.GameFinishedDialog
import com.development_felber.dartapp.ui.theme.DartAppTheme

data class MultiplayerGamePlayerStats(
    val overallAverage: Double,
    val overallDoubleRate: Double,
)

@Composable
fun MultiplayerGameFinishedDialog(
    players: List<PlayerUiState>,
    stats: List<MultiplayerGamePlayerStats>,
    onMenuClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit,
) {
    assert(players.size == stats.size && players.size == 2) {
        "MultiplayerGameFinishedDialog only supports 2 players"
    }

    GameFinishedDialog(
        title = "Game finished!",
        onMoreDetailsClicked = { /*TODO*/ },
        moreDetailsEnabled = false,
        onMenuClicked = onMenuClicked,
        onPlayAgainClicked = onPlayAgainClicked
    ) {
        MultiplayerStatsContent(
            players = players,
            stats = stats,
        )
    }
}

@Composable
fun MultiplayerStatsContent(
   players: List<PlayerUiState>,
   stats: List<MultiplayerGamePlayerStats>,
) {
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
            leftValueString = "%.1f".format(stats[0].overallAverage),
            rightValueString = "%.1f".format(stats[1].overallAverage),
        )

        MultiplayerStatsRow(
            name = "Double Rate",
                leftValueString = "%.0f%%".format(stats[0].overallDoubleRate * 100),
            rightValueString = "%.0f%%".format(stats[1].overallDoubleRate * 100),
        )
    }
}

@Composable
private fun MultiplayerStatsRow(
    name: String,
    leftValueString: String,
    rightValueString: String,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = leftValueString,
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )

        Text(
            text = rightValueString,
            style = MaterialTheme.typography.titleLarge,
        )
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
                ),
                PlayerUiState(
                    playerRole = PlayerRole.Two,
                    name = "Player 2",
                    score = PlayerScore(2, 3, 1, 2),
                    average = 0.0,
                    dartCount = 0,
                    pointsLeft = 0,
                    last = 0,
                ),
            ),
            stats = listOf(
                MultiplayerGamePlayerStats(
                    overallAverage = 50.0,
                    overallDoubleRate = 0.6,
                ),
                MultiplayerGamePlayerStats(
                    overallAverage = 61.2,
                    overallDoubleRate = 0.1,
                ),
            ),
            onMenuClicked = { },
            onPlayAgainClicked = { },
        )
    }
}
