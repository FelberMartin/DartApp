package com.development_felber.dartapp.ui.screens.home.dialogs.new_feature

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme


@Composable
fun MultiplayerAddedDialog(
    viewModel: MultiplayerAddedDialogViewModel,
) {
    val showDialog by viewModel.showDialog.collectAsState()
    if (showDialog) {
        MultiplayerAddedDialogContent {
            viewModel.dismissDialog()
        }
    }
}


@Composable
fun MultiplayerAddedDialogContent(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Multipalyer is here!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(horizontal =  24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You can now challenge your friends to determine once" +
                            " and for all who is the king!",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Dedicated statistics for Multiplayer games may get added in future updates.",
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,

                    )
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Got it!",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun MultiplayerAddedDialogPreview() {
    DartAppTheme() {
        MultiplayerAddedDialogContent(onDismiss = {})
    }
}