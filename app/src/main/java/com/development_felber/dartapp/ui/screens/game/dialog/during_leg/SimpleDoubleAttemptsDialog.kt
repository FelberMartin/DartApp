package com.development_felber.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SimpleDoubleAttemptsDialog(
    onAttemptClicked: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Double Attempt?")
        },
        text = {
            Text("Did you attempt to hit a double? Used for double-rate Statistics.")
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onAttemptClicked(false) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "No"
                    )
                }

                Button(
                    onClick = { onAttemptClicked(true) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Yes"
                    )
                }
            }

        }
    )
}

@Preview
@Composable
private fun SimpleDoubleAttemptsDialogPreview() {
    SimpleDoubleAttemptsDialog(
        onAttemptClicked = {}
    )
}