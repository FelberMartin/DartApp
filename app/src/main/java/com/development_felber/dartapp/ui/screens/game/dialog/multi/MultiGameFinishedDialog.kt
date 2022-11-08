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
import com.development_felber.dartapp.ui.screens.game.dialog.GameFinishedDialog
import com.development_felber.dartapp.ui.screens.home.dialogs.Player
import com.development_felber.dartapp.ui.theme.DartAppTheme


data class MultiplayerStatsInfo(
    val player1: Player,
    val player2: Player,
    val combinedScore: CombinedScore,
    val avgScore1: Double,
    val avgScore2: Double,
    val doubleRate1: Double,
    val doubleRate2: Double,
)

@Composable
fun MultiplayerGameFinishedDialog(
    onMenuClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit,
    multiplayerStatsInfo: MultiplayerStatsInfo
) {
    GameFinishedDialog(
        title = "Game finished!",
        onMoreDetailsClicked = { /*TODO*/ },
        moreDetailsEnabled = false,
        onMenuClicked = onMenuClicked,
        onPlayAgainClicked = onPlayAgainClicked
    ) {
        MultiplayerStatsContent(
            multiplayerStatsInfo = multiplayerStatsInfo
        )
    }
}

@Composable
fun MultiplayerStatsContent(
    multiplayerStatsInfo: MultiplayerStatsInfo
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        ScoreComparison(
            player1 = multiplayerStatsInfo.player1,
            player2 = multiplayerStatsInfo.player2,
            combinedScore = multiplayerStatsInfo.combinedScore
        )

        MultiplayerStatsRow(
            name = "Avg",
            leftValueString = "%.1f".format(multiplayerStatsInfo.avgScore1),
            rightValueString = "%.1f".format(multiplayerStatsInfo.avgScore2)
        )

        MultiplayerStatsRow(
            name = "Double Rate",
            leftValueString = "%.0f%%".format(multiplayerStatsInfo.doubleRate1),
            rightValueString = "%.0f%%".format(multiplayerStatsInfo.doubleRate2)
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
            onMenuClicked = { /*TODO*/ },
            onPlayAgainClicked = { /*TODO*/ },
            multiplayerStatsInfo = MultiplayerStatsInfo(
                player1 = Player("Player 1"),
                player2 = Player("Player 2"),
                combinedScore = CombinedScore(3, 2, 3, 2, 0, 2),
                avgScore1 = 20.0,
                avgScore2 = 20.0,
                doubleRate1 = 50.0,
                doubleRate2 = 50.0,
            )
        )
    }
}
