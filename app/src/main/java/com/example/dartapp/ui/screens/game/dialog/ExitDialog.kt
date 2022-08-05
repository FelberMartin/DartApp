package com.example.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExitDialog(
    onDismissDialog: () -> Unit,
    onExitClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = {
            Text("Exit Training?")
        },
        text = {
            Text("Your progress will not be saved.")
        },
        confirmButton = {
            Row() {
                OutlinedButton(onClick = onExitClicked) {
                    Text("Exit")
                }

                Spacer(Modifier.width(12.dp))

                Button(onClick = onDismissDialog) {
                    Text("Cancel")
                }
            }

        }
    )
}

@Preview
@Composable
private fun ExitDialogPreview() {
    ExitDialog(
        onDismissDialog = {}
    ) {}
}