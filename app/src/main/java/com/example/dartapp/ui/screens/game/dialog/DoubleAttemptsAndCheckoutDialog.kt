package com.example.dartapp.ui.screens.game.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class DoubleAttemptsAndCheckoutDialogResult(
    val doubleAttempts: Int?,
    val checkout: Int?
)

@Composable
fun DoubleAttemptsAndCheckoutDialog(
    askForDoubleAttempts: Boolean,
    askForCheckout: Boolean,
    minDartCount: Int? = null,
    onDialogCancelled: () -> Unit,
    onDialogConfirmed: (DoubleAttemptsAndCheckoutDialogResult) -> Unit
) {
    val dialogTitle = if (askForDoubleAttempts && askForCheckout) {
        "Double Attempts & Checkout"
    } else if (askForDoubleAttempts) {
        "Double Attempts"
    } else {
        "Checkout"
    }
    var doubleAttempts by remember { mutableStateOf(1) }
    var checkoutDarts by remember { mutableStateOf(3) }

    val disabledDoubleAttempts = getDisabledDoubleNumbers(
        finished = askForCheckout,
        checkoutDarts = checkoutDarts,
        minDartCount = minDartCount
    )
    val disabledCheckoutNumbers = getDisabledCheckoutNumbers(
        doubleAttempts = doubleAttempts,
        minDartCount = minDartCount
    )

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(dialogTitle)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (askForDoubleAttempts) {
                    Text("Number of attempted double-finishes. Used for double-rate Statistics.")
                    NumberSelectionRow(
                        numbers = listOf(0, 1, 2, 3),
                        disabledNumbers = disabledDoubleAttempts,
                        selectedNumber = doubleAttempts,
                        onSelectionChanged = { doubleAttempts = it }
                    )
                }

                if (askForDoubleAttempts && askForCheckout) {
                    Divider(Modifier.padding(vertical = 4.dp))
                }

                if (askForCheckout) {
                    Text("Number of darts needed for checkout.")
                    NumberSelectionRow(
                        numbers = listOf(1, 2, 3),
                        disabledNumbers = disabledCheckoutNumbers,
                        selectedNumber = checkoutDarts,
                        onSelectionChanged = { checkoutDarts = it }
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDialogConfirmed(
                DoubleAttemptsAndCheckoutDialogResult(
                    doubleAttempts = if (askForDoubleAttempts) doubleAttempts else null,
                    checkout = if (askForCheckout) checkoutDarts else null
                )
            ) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDialogCancelled,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun getDisabledDoubleNumbers(finished: Boolean, checkoutDarts: Int, minDartCount: Int?): List<Int> {
    val disabled = mutableListOf<Int>()
    if (finished) {
        disabled.add(0)
    }
    var lastNumberAvailable = checkoutDarts
    if (minDartCount != null) {
        lastNumberAvailable = (checkoutDarts - minDartCount) + 1
    }
    for (i in (lastNumberAvailable + 1)..3) {
        disabled.add(i)
    }
    return disabled
}

private fun getDisabledCheckoutNumbers(doubleAttempts: Int, minDartCount: Int?): List<Int> {
    val disabled = mutableListOf<Int>()
    var firstNumberAvailable = doubleAttempts
    if (minDartCount != null) {
        firstNumberAvailable = (minDartCount - 1) + doubleAttempts
    }
    for (i in 1 until firstNumberAvailable) {
        disabled.add(i)
    }
    return disabled
}


@Composable
private fun NumberSelectionRow(
    numbers: List<Int>,
    disabledNumbers: List<Int>,
    selectedNumber: Int,
    onSelectionChanged: (Int) -> Unit
) {
    val selectedButtonColors = ButtonDefaults.buttonColors()
    val unselectedButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            for (i in numbers) {
                val selected = i == selectedNumber
                Button(
                    onClick = { if (!selected) onSelectionChanged(i) },
                    enabled = !disabledNumbers.contains(i),
                    shape = MaterialTheme.shapes.medium,
                    colors = if (selected) selectedButtonColors else unselectedButtonColors,
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("$i")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DialogPreview() {
    DoubleAttemptsAndCheckoutDialog(
        askForDoubleAttempts = true,
        askForCheckout = true,
        minDartCount = 3,
        onDialogCancelled = {},
        onDialogConfirmed = {}
    )
}