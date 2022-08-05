package com.example.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun LegFinishedDialog(
    onPlayAgainClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Leg finished!")
        },
        text = {
            Text("Would you like play again?")
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onMenuClicked) {
                    Text("Back to Menu")
                }

                Spacer(Modifier.width(12.dp))

                Button(onClick = onPlayAgainClicked) {
                    Text("Play again")
                }
            }

        }
    )
}

@Preview
@Composable
private fun LegFinishedDialogPreview() {
    LegFinishedDialog(
        onMenuClicked = {},
        onPlayAgainClicked = {}
    )
}