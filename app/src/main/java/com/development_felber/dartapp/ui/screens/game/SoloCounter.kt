package com.development_felber.dartapp.ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.shared.MyCard

@Composable
fun SoloPlayerStatsCard(
    playerState: PlayerUiState,
) {
    MyCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = playerState.pointsLeft.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerGameStatistic(
                    name = "Darts:",
                    valueString = playerState.dartCount.toString()
                )

                PlayerGameStatistic(
                    name = "Last:",
                    valueString = playerState.last?.toString() ?: "-",
                )

                PlayerGameStatistic(
                    name = "Ø",
                    valueString = playerState.average?.let { "%.2f".format(it) } ?: "-",
                )
            }
        }
    }
}

@Composable
fun PlayerGameStatistic(
    name: String,
    valueString: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name ",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = valueString,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
