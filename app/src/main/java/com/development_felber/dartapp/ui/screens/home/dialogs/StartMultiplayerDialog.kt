@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.data.persistent.database.player.PlayerDao
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun StartMultiplayerDialogViewModelEntryPoint(
    viewModel: StartMultiplayerDialogViewModel,
) {
    val players by viewModel.players.collectAsState()
    val player1 by viewModel.player1.collectAsState()
    val player2 by viewModel.player2.collectAsState()
    val legCount by viewModel.legCount.collectAsState()
    val setCount by viewModel.setCount.collectAsState()

    StartMultiplayerDialog(
        allPlayers = players,
        player1 = player1,
        player2 = player2,
        onPlayer1Changed = { viewModel.setPlayer(PlayerPosition.PLAYER_1, it) },
        onPlayer2Changed = { viewModel.setPlayer(PlayerPosition.PLAYER_2, it) },
        onNewPlayerCreated = viewModel::onCreateNewPlayer,
        legCount = legCount,
        setCount = setCount,
        onLegCountChanged = viewModel::setLegCount,
        onSetCountChanged = viewModel::setSetCount,
        onStartClick = viewModel::onDialogConfirmed,
        onCancel = viewModel::onDialogCancelled,
    )
}


@Composable
fun StartMultiplayerDialog(
    allPlayers: List<Player>,
    player1: Player?,
    player2: Player?,
    onPlayer1Changed: (Player?) -> Unit,
    onPlayer2Changed: (Player?) -> Unit,
    onNewPlayerCreated: (String, PlayerPosition) -> Unit,
    legCount: Int,
    setCount: Int,
    onLegCountChanged: (Int) -> Unit,
    onSetCountChanged: (Int) -> Unit,
    onStartClick: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(32.dp),
    ) {
        DialogTitle()
        Column {
            PlayersSelection(
                allPlayers = allPlayers,
                player1 = player1,
                player2 = player2,
                onPlayer1Changed = onPlayer1Changed,
                onPlayer2Changed = onPlayer2Changed,
                onNewPlayerCreated = onNewPlayerCreated,
            )
            Spacer(modifier = Modifier.height(48.dp))
            LegAndSetSelection(
                legCount = legCount,
                onLegCountChanged = onLegCountChanged,
                setCount = setCount,
                onSetCountChanged = onSetCountChanged,
            )
        }

        CancelConfirmButtonRow(
            onCancel = onCancel,
            onConfirm = onStartClick,
        )
    }
}

@Composable
private fun DialogTitle() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = "Multiplayer",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun CancelConfirmButtonRow(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextButton(onClick = onCancel) {
            Text("Cancel")
        }

        Spacer(Modifier.width(32.dp))

        Button(onClick = onConfirm) {
            Text("Let's go!")
        }
    }
}

@Composable
private fun ColumnScope.PlayersSelection(
    allPlayers: List<Player>,
    player1: Player?,
    player2: Player?,
    onPlayer1Changed: (Player?) -> Unit,
    onPlayer2Changed: (Player?) -> Unit,
    onNewPlayerCreated: (String, PlayerPosition) -> Unit,
) {
    HeaderText(text = "Players")

    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .height(144.dp)
            .fillMaxWidth()
    ) {
        PlayerSelectionInfoColumn()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        ) {
            PlayerSelection(
                allPlayers = allPlayers,
                selectedPlayer = player1,
                onNewPlayerCreated = { onNewPlayerCreated(it, PlayerPosition.PLAYER_1) },
                onSelectedPlayerChanged = onPlayer1Changed,
                invalidPlayer = player2,
            )

            PlayerSwap {
                onPlayer1Changed(player2)
                onPlayer2Changed(player1)
            }

            PlayerSelection(
                allPlayers = allPlayers,
                selectedPlayer = player2,
                onNewPlayerCreated = { onNewPlayerCreated(it, PlayerPosition.PLAYER_2) },
                onSelectedPlayerChanged = onPlayer2Changed,
                invalidPlayer = player1,
            )
        }
    }

}

@Composable
private fun HeaderText(text: String) = Text(
    text = text,
    style = MaterialTheme.typography.headlineSmall,
    modifier = Modifier.padding(bottom = 16.dp)
)


@Composable
private fun PlayerSelectionInfoColumn() {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight()
    ) {
        PlayerSelectionInfoColumnText(text = "Player 1")
        PlayerSelectionInfoColumnText(text = "vs.")
        PlayerSelectionInfoColumnText(text = "Player 2")
    }
}

@Composable
private fun PlayerSelectionInfoColumnText(
    text: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(48.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun PlayerSelection(
    selectedPlayer: Player?,
    onSelectedPlayerChanged: (Player) -> Unit,
    allPlayers: List<Player>,
    invalidPlayer: Player?,
    onNewPlayerCreated: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showNewPlayerDialog by remember { mutableStateOf(false) }

    SuggestionChip(
        onClick = {
            if (selectedPlayer == null) {
                showNewPlayerDialog = true
            } else {
                isExpanded = !isExpanded
            }
        },
        label = {
            Row () {
                Text(
                    text = selectedPlayer?.name ?: "New Player",
                    modifier = Modifier.weight(1f),
                )

                Spacer(Modifier.width(8.dp))
                val icon = if (selectedPlayer == null) {
                    Icons.Default.Add
                } else if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Other players"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )

    PlayerSelectionDropDownMenu(
        isExpanded = isExpanded,
        allPlayers = allPlayers,
        invalidPlayer = invalidPlayer,
        selectedPlayer = selectedPlayer,
        onSelectedPlayerChanged = onSelectedPlayerChanged,
        onCreateNewPlayerClick = {
            showNewPlayerDialog = true
        },
    )

    AnimatedVisibility(visible = showNewPlayerDialog) {
       CreateNewPlayerDialog(
            onNewPlayerCreated = onNewPlayerCreated,
            onCancel = { showNewPlayerDialog = false },
        )
    }
}

@Composable
private fun PlayerSelectionDropDownMenu(
    isExpanded: Boolean,
    allPlayers: List<Player>,
    invalidPlayer: Player?,
    selectedPlayer: Player?,
    onSelectedPlayerChanged: (Player) -> Unit,
    onCreateNewPlayerClick: () -> Unit,
) {
    var isExpanded1 = isExpanded
    DropdownMenu(
        expanded = isExpanded1,
        onDismissRequest = { isExpanded1 = false },
    ) {
        for (player in allPlayers) {
            DropdownMenuItem(
                onClick = {
                    isExpanded1 = false
                    onSelectedPlayerChanged(player)
                },
                text = {
                    Text(
                        text = player.name,
                        color = if (player == selectedPlayer) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                },
                enabled = player != invalidPlayer,
            )
        }

        DropdownMenuItem(
            onClick = {
                isExpanded1 = false
                onCreateNewPlayerClick()
            },
            text = {
                Text("New Player")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Player",
                )
            }
        )
    }
}


@Composable
private fun PlayerSwap(
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.SwapVert,
            contentDescription = "Swap players",
        )
    }
}


@Composable
private fun ColumnScope.LegAndSetSelection(
    legCount: Int,
    onLegCountChanged: (Int) -> Unit,
    setCount: Int,
    onSetCountChanged: (Int) -> Unit,
) {
    HeaderText(text = "Game Settings")

    StepperRow(
        text = "Legs to win a Set",
        value = legCount,
        onValueChanged = onLegCountChanged,
    )

    StepperRow(
        text = "Sets to win the game",
        value = setCount,
        onValueChanged = onSetCountChanged,
    )
}

@Composable
fun StepperRow(
    text: String,
    value: Int,
    onValueChanged: (Int) -> Unit,
    minValue: Int = 1,
    maxValue: Int = 5
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(
            Modifier
                .width(8.dp)
                .weight(1f))

        Stepper(
            value = value,
            onValueChange = onValueChanged,
            minValue = minValue,
            maxValue = maxValue
        )
    }
}


@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun StartMultiplayerDialogPreview() {
    DartAppTheme() {
        val allPlayers  = listOf(
            Player(name = "Player 1"),
            Player(name = "Player 2"),
            Player(name = "Player 3"),
            Player(name = "Player 4"),
        )

        var player1 by remember { mutableStateOf<Player?>(allPlayers[0]) }
        var player2 by remember { mutableStateOf<Player?>(allPlayers[1]) }
        var legCount by remember { mutableStateOf(3) }
        var setCount by remember { mutableStateOf(2) }

        StartMultiplayerDialog(
            allPlayers = allPlayers,
            player1 = player1,
            player2 = player2,
            onPlayer1Changed = { player1 = it },
            onPlayer2Changed = { player2 = it },
            legCount =  legCount,
            onLegCountChanged = { legCount = it },
            setCount = setCount,
            onSetCountChanged = { setCount = it },
            onNewPlayerCreated = { _, _ ->},
            onStartClick = {},
            onCancel = {},
        )
    }
}
