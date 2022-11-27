@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.game.PlayerRole
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
        onPlayer1Changed = { viewModel.setPlayer(PlayerRole.One, it) },
        onPlayer2Changed = { viewModel.setPlayer(PlayerRole.Two, it) },
        onNewPlayerCreated = viewModel::onCreateNewPlayer,
        validateNewPlayerName = viewModel::isNewPlayerNameValid,
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
    onNewPlayerCreated: (String, PlayerRole) -> Unit,
    validateNewPlayerName:(String) -> Result<Unit>,
    legCount: Int,
    setCount: Int,
    onLegCountChanged: (Int) -> Unit,
    onSetCountChanged: (Int) -> Unit,
    onStartClick: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(32.dp),
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
                validateNewPlayerName = validateNewPlayerName,
            )
            Spacer(modifier = Modifier.height(48.dp))
            LegAndSetSelection(
                legCount = legCount,
                onLegCountChanged = onLegCountChanged,
                setCount = setCount,
                onSetCountChanged = onSetCountChanged,
            )
        }

        Spacer(Modifier.height(12.dp))
        CancelConfirmButtonRow(
            enabled = player1 != null && player2 != null,
            onCancel = onCancel,
            onConfirm = onStartClick,
        )
    }
}

@Composable
private fun DialogTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Multiplayer Game",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun CancelConfirmButtonRow(
    enabled: Boolean,
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

        Button(
            onClick = onConfirm,
            enabled = enabled,
        ) {
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
    onNewPlayerCreated: (String, PlayerRole) -> Unit,
    validateNewPlayerName:(String) -> Result<Unit>,
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
                onNewPlayerCreated = { onNewPlayerCreated(it, PlayerRole.One) },
                onSelectedPlayerChanged = onPlayer1Changed,
                invalidPlayer = player2,
                validateNewPlayerName = validateNewPlayerName,
            )

            PlayerSwap {
                onPlayer1Changed(player2)
                onPlayer2Changed(player1)
            }

            PlayerSelection(
                allPlayers = allPlayers,
                selectedPlayer = player2,
                onNewPlayerCreated = { onNewPlayerCreated(it, PlayerRole.Two) },
                onSelectedPlayerChanged = onPlayer2Changed,
                invalidPlayer = player1,
                validateNewPlayerName = validateNewPlayerName,
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
    validateNewPlayerName:(String) -> Result<Unit>,
    onNewPlayerCreated: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showNewPlayerDialog by remember { mutableStateOf(false) }
    val noValidPlayerInDropdown = allPlayers.none { it != invalidPlayer }

    Box() {
        SuggestionChip(
            onClick = {
                if (noValidPlayerInDropdown) {
                    showNewPlayerDialog = true
                } else {
                    isExpanded = !isExpanded
                }
            },
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val text = if (noValidPlayerInDropdown) {
                        "New Player"
                    } else {
                        selectedPlayer?.name ?: "Select player..."
                    }
                    Text(
                        text = text,
                        modifier = Modifier.weight(1f),
                        color = if (selectedPlayer == null && !noValidPlayerInDropdown) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )

                    Spacer(Modifier.width(8.dp))
                    val icon = if (noValidPlayerInDropdown) {
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
            border = SuggestionChipDefaults.suggestionChipBorder(
                borderColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )

        PlayerSelectionDropDownMenu(
            isExpanded = isExpanded,
            allPlayers = allPlayers,
            invalidPlayer = invalidPlayer,
            selectedPlayer = selectedPlayer,
            onSelectedPlayerChanged = {
                onSelectedPlayerChanged(it)
                isExpanded = false
            },
            onCreateNewPlayerClick = {
                showNewPlayerDialog = true
            },
            onDismiss = {
                isExpanded = false
            }
        )
    }

    AnimatedVisibility(visible = showNewPlayerDialog) {
       CreateNewPlayerDialog(
            onNewPlayerCreated = {
                onNewPlayerCreated(it)
                showNewPlayerDialog = false
            },
            onCancel = { showNewPlayerDialog = false },
            validateName = validateNewPlayerName,
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
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
    ) {
        for (player in allPlayers) {
            DropdownMenuItem(
                onClick = {
                    onDismiss()
                    onSelectedPlayerChanged(player)
                },
                text = {
                    Text(
                        text = player.name,
                        color = if (player == selectedPlayer) {
                            MaterialTheme.colorScheme.primary
                        } else if (player == invalidPlayer) {
                            MaterialTheme.colorScheme.outline
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
                onDismiss()
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
    var rotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(targetValue = rotation)

    IconButton(onClick = {
        onClick()
        rotation = (rotation + 180f) % 360f
    }) {
        Icon(
            imageVector = Icons.Default.SwapVert,
            contentDescription = "Swap players",
            modifier = Modifier.rotate(animatedRotation)
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

    Spacer(Modifier.height(12.dp))

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


@Preview(showBackground = true, widthDp = 380, heightDp = 800)
@Composable
private fun StartMultiplayerDialogPreview() {
    DartAppTheme() {
        val allPlayers  = listOf(
            Player(name = "Player 1"),
            Player(name = "Player 2"),
            Player(name = "Player 3"),
            Player(name = "Player 4"),
        )

        var player1 by remember { mutableStateOf<Player?>(null) }
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
            validateNewPlayerName = { Result.success(Unit) },
        )
    }
}
