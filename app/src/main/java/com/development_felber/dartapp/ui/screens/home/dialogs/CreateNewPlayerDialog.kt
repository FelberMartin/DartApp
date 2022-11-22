@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.home.dialogs

import android.inputmethodservice.Keyboard
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme


@Composable
fun CreateNewPlayerDialog(
    onNewPlayerCreated: (String) -> Unit,
    validateName:(String) -> Result<Unit>,
    onCancel: () -> Unit,
) {
    var playerName by remember { mutableStateOf("") }
    val result = validateName(playerName)

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Create new player",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth(),
            ) {
                TextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    placeholder = {
                        Text(
                            text = "Player name",
                            color = MaterialTheme.colorScheme.outline,
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (result.isSuccess) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Valid name",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 32.dp)
                )

                Spacer(Modifier.height(4.dp))
                InfoField(result = result)
            }
        },
        confirmButton = {
            Button(
                enabled = result.isSuccess,
                onClick = {
                onNewPlayerCreated(playerName)
            }) {
                Text(text = "Create")
            }
        },
    )
}

@Composable
private fun InfoField(
    result: Result<Unit>,
) {
    if (result.isFailure) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Invalid name",
                tint = MaterialTheme.colorScheme.error,
            )

            Text(
                text = result.exceptionOrNull()?.message ?: "",
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun CreateNewPlayerDialogPreview() {
    DartAppTheme() {
        CreateNewPlayerDialog(onNewPlayerCreated = {}, onCancel = {}, validateName = { Result.success(Unit) })
    }
}