package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

data class Player(
    val name: String
)

data class StartMultiplayerDialogResult(
    val player1: Player,
    val player2: Player,
    val legCount: Int,
    val setCount: Int = 1,
)

@Composable
fun StartMultiplayerDialog(
    allPlayers: List<Player>,
    lastPlayer1: Player?,
    lastPlayer2: Player?,
    onNewPlayerCreated: (String) -> Unit,
    onStart: (StartMultiplayerDialogResult) -> Unit,
    onCancel: () -> Unit,
) {
    val legCount = remember { mutableStateOf(3) }
    val setCount = remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = { },
        text = {
            Column() {
                LegAndSetSelection(
                    legCount = legCount,
                    setCount = setCount,
                )
            }
        },
        confirmButton = {
            CancelConfirmButtonRow(
                onCancel = onCancel,
                onConfirm = {
                    onStart(
                        StartMultiplayerDialogResult(
                            // TODO: Get player1 and player2
                            player1 = lastPlayer1 ?: allPlayers.first(),
                            player2 = lastPlayer2 ?: allPlayers.first(),
                            legCount = legCount.value,
                            setCount = setCount.value,
                        )
                    )
                },
            )
        }
    )
}

@Composable
private fun CancelConfirmButtonRow(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Row() {
        FilledTonalButton(onClick = onCancel) {
            Text("Cancel")
        }

        Spacer(Modifier.width(8.dp))

        Button(onClick = onConfirm) {
            Text("Let's go!")
        }
    }
}

@Composable
private fun ColumnScope.PlayersSelection(
    allPlayers: List<Player>,
    player1: Player?,
    player2: Player?,
    onPlayer1Changed: (Player?) -> Unit,
    onPlayer2Changed: (Player?) -> Unit,
    onNewPlayerCreated: (String) -> Unit,
) {
    HeaderText(text = "Players")

    PlayerSelectionInfoRow()

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        PlayerSelection(
            allPlayers = allPlayers,
            lastPlayer = lastPlayer1,
            onNewPlayerCreated = onNewPlayerCreated,
        )

        PlayerSwap {
            onPlayer1Changed(player2)
            onPlayer2Changed(player1)
        }

        PlayerSelection(
            allPlayers = allPlayers,
            lastPlayer = lastPlayer2,
            onNewPlayerCreated = onNewPlayerCreated,
        )
    }
}

@Composable
private fun HeaderText(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.headlineSmall,
)


@Composable
private fun PlayerSelectionInfoRow() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        PlayerSelectionInfoRowText(text = "Player 1")
        PlayerSelectionInfoRowText(text = "vs.")
        PlayerSelectionInfoRowText(text = "Player 2")
    }
}

@Composable
private fun PlayerSelectionInfoRowText(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun PlayerSelection(
    selectedPlayer: Player?,
    onSelectedPlayerChanged: (Player) -> Unit,
    allPlayers: List<Player>,
    onNewPlayerCreated: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val playerNames = allPlayers.map { it.name }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false },
    ) {
        DropdownMenuItem(
            onClick = {
                isExpanded = false
                onNewPlayerCreated("New Player")
            }
        ) {
            Text("New Player")
        }

        DropdownMenuItem(
            onClick = {
                isExpanded = false
                onNewPlayerCreated("New Player 2")
            }
        ) {
            Text("New Player 2")
        }

        DropdownMenuItem(
            onClick = {
                isExpanded = false
                onNewPlayerCreated("New Player 3")
            }
        ) {
            Text("New Player 3")
        }

        DropdownMenuItem(
            onClick = {
                isExpanded = false
                onNewPlayerCreated("New Player 4")
            }
        ) {
            Text("New Player 4")
        }

        DropdownMenuItem(
            onClick = {
                isExpanded = false
                onNewPlayerCreated("New Player 5")
            }
        ) {
            Text("New Player 5")
        }
    }

    OutlinedTonalButton(
        onClick = { isExpanded = true },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = selectedPlayer?.name ?: "Select Player",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun PlayerSwap(
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap players",
        )
    }
}


@Composable
private fun ColumnScope.LegAndSetSelection(
    legCount: MutableState<Int>,
    setCount: MutableState<Int>,
) {
    HeaderText(text = "Game Settings")

    StepperRow(
        text = "Legs to win a Set",
        value = legCount.value,
        onValueChanged = { legCount.value = it },
    )

    StepperRow(
        text = "Sets to win the game",
        value = setCount.value,
        onValueChanged = { setCount.value = it },
    )
}

@Composable
fun StepperRow(
    text: String,
    value: Int,
    onValueChanged: (Int) -> Unit,
    minValue: Int = 1,
    maxValue: Int = 5
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(Modifier.width(8.dp))

        Stepper(
            value = value,
            onValueChange = onValueChanged,
            minValue = minValue,
            maxValue = maxValue
        )
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun StartMultiplayerDialogPreview() {
    DartAppTheme() {
        StartMultiplayerDialog(
            allPlayers = listOf(
                Player("Player 1"),
                Player("Player 2"),
                Player("Player 3"),
                Player("Player 4"),
            ),
            lastPlayer1 = null,
            lastPlayer2 = null,
            onNewPlayerCreated = {},
            onStart = {},
            onCancel = {},
        )
    }
}
