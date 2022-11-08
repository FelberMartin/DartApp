@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun CreateNewPlayerDialog(
    onNewPlayerCreated: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var playerName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Create new player",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
           TextField(
               value = playerName,
               onValueChange = { playerName = it },
               placeholder = {
                   Text(
                       text = "Player name",
                       color = MaterialTheme.colorScheme.outline,
                   )
             },
               modifier = Modifier.padding(top = 32.dp)
           )
        },
        confirmButton = {
            Button(
                enabled = playerName.isNotBlank(),
                onClick = {
                onNewPlayerCreated(playerName)
            }) {
                Text(text = "Create")
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun CreateNewPlayerDialogPreview() {
    DartAppTheme() {
        CreateNewPlayerDialog(onNewPlayerCreated = {}, onCancel = {})

    }
}