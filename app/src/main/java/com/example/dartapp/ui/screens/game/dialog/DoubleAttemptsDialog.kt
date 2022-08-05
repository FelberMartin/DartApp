package com.example.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DoubleAttemptsDialog(
    onNumberClicked: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Double Attempts")
        },
        text = {
            Text("Number of attempted double-finishes. Used for double-rate Statistics.")
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 0..3) {
                    Button(
                        onClick = { onNumberClicked(i) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("$i")
                    }
                }
            }

        }
    )
}

@Preview
@Composable
private fun DoubleAttemptsDialogPreview() {
    DoubleAttemptsDialog(
        onNumberClicked = {}
    )
}