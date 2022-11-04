package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    AlertDialog(
        onDismissRequest = { },
        text = {
            Column() {

            }
        },
        confirmButton = {
            CancelConfirmButtonRow(
                onCancel = onCancel,
                onConfirm = {

                })
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
private fun ColumnScope.LegAndSetSelection(
    legCount: MutableState<Int>,
    setCount: MutableState<Int>,
) {
    Text(
        text = "Game Settings",
        style = MaterialTheme.typography.headlineMedium
    )

    // TODO: Maybe use segmented buttons here as well, instead of steppers
}

@Composable
fun StepperRow(
    text: String,
    value: Int,
    minValue: Int = 1,
    maxValue: Int = 5
) {

}