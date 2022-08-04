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
fun CheckoutDialog(
    dialogOpen: Boolean,
    minimumRequiredDarts: Int,
    onNumberClicked: (Int) -> Unit
) {
    if (!dialogOpen) {
        return
    }

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text("Checkout")
        },
        text = {
            Text("Number of darts needed for checkout.")
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..3) {
                    Button(
                        onClick = { onNumberClicked(i) },
                        enabled = i >= minimumRequiredDarts,
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
private fun CheckoutDialogPreview() {
    CheckoutDialog(
        dialogOpen = true,
        minimumRequiredDarts = 2,
        onNumberClicked = {}
    )
}