@file:OptIn(ExperimentalAnimationApi::class)

package com.development_felber.dartapp.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.development_felber.dartapp.data.persistent.database.finished_leg.FakeFinishedLegDao
import com.development_felber.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.dialog.*
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialog
import com.development_felber.dartapp.ui.screens.game.dialog.solo.LegFinishedDialogEntryPoint
import com.development_felber.dartapp.ui.shared.Background
import com.development_felber.dartapp.ui.shared.KeepScreenOn
import com.development_felber.dartapp.ui.shared.MyCard
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.development_felber.dartapp.ui.values.Padding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow


@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    KeepScreenOn()
    BackHandler() {
        viewModel.onCloseClicked()
    }

    val gameUiState by viewModel.gameUiState.collectAsState()

    GameScreenContent(
        gameUiState = gameUiState,
        dartOrServeEnteredFlow = viewModel.dartOrServeEnteredFlow,
        onCloseClicked = viewModel::onCloseClicked,
        onUndoClicked = viewModel::onUndoClicked,
        onSwapNumberPadClicked = viewModel::onSwapNumberPadClicked,
        onNumberTyped = viewModel::onNumberTyped,
        onModifierToggled = viewModel::onModifierToggled,
        clearNumberPad = viewModel::clearNumberPad,
        onEnterClicked = viewModel::onEnterClicked,
    )

    val dialogUiState = viewModel.gameUiState.collectAsState().value.dialogUiState
    DialogsOverlay(
        dialogUiState = dialogUiState,
        gameViewModel = viewModel,
    )
}

@Composable
private fun GameScreenContent(
    gameUiState: GameUiState,
    dartOrServeEnteredFlow: Flow<Int>,
    onCloseClicked: () -> Unit,
    onUndoClicked: () -> Unit,
    onSwapNumberPadClicked: () -> Unit,
    onNumberTyped: (Int) -> Unit,
    onModifierToggled: (PerDartNumberPad.Modifier) -> Unit,
    clearNumberPad: () -> Unit,
    onEnterClicked: () -> Unit,
) {
    Background {
        Column(Modifier.fillMaxSize()) {
            TopRow(onCloseClicked = onCloseClicked)

            Column(
                modifier = Modifier
                    .padding(Padding.MediumPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                PlayerStats(
                    currentPlayerRole = gameUiState.currentPlayer,
                    playerUiStates = gameUiState.playerUiStates,
                )

                Spacer(Modifier.height(20.dp))
                BottomElements(
                    numberPadUiState = gameUiState.numberPadUiState,
                    checkoutTip = gameUiState.checkoutTip,
                    dartOrServeEnteredFlow = dartOrServeEnteredFlow,
                    onUndoClicked = onUndoClicked,
                    onSwapNumberPadClicked = onSwapNumberPadClicked,
                    onNumberTyped = onNumberTyped,
                    onModifierToggled = onModifierToggled,
                    clearNumberPad = clearNumberPad,
                    onEnterClicked = onEnterClicked,
                )
            }
        }
    }
}

@Composable
private fun TopRow(
    onCloseClicked: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, end = 16.dp)) {
        Spacer(Modifier.weight(1f))

        IconButton(onClick = onCloseClicked) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
            )
        }
    }
}

@Composable
private fun PlayerStats(
    currentPlayerRole: PlayerRole,
    playerUiStates: List<PlayerUiState>,
) {
    if (playerUiStates.size == 1) {
        SoloPlayerStatsCard(
            playerState = playerUiStates[0]
        )
    } else if (playerUiStates.size == 2) {
        CombinedMultiplayerPlayerCounter(
            playerStateLeft = playerUiStates[0],
            playerStateRight = playerUiStates[1],
            currentPlayerRole = currentPlayerRole,
        )
    }
}

@Composable
private fun BottomElements(
    numberPadUiState: NumberPadUiState,
    checkoutTip: String?,
    dartOrServeEnteredFlow: Flow<Int>,
    onUndoClicked: () -> Unit,
    onSwapNumberPadClicked: () -> Unit,
    onNumberTyped: (Int) -> Unit,
    onModifierToggled: (PerDartNumberPad.Modifier) -> Unit,
    clearNumberPad: () -> Unit,
    onEnterClicked: () -> Unit,
) {
    Column() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CheckoutInfo(checkoutTip)

            NumPadInfoAndActionsRow(
                onUndoClicked = onUndoClicked,
                numberState = numberPadUiState.numberPad.number.collectAsState(),
                onSwapNumberPadClicked = onSwapNumberPadClicked,
                isEnterDisabled = !numberPadUiState.enterEnabled,
                dartOrServeEnteredFlow = dartOrServeEnteredFlow
            )

            PickNumberPadVersion(
                numberPadUiState = numberPadUiState,
                onNumberTyped = onNumberTyped,
                onModifierToggled = onModifierToggled,
                clearNumberPad = clearNumberPad,
                onEnterClicked = onEnterClicked,
            )
        }
    }
}

@Composable
private fun PickNumberPadVersion(
    numberPadUiState: NumberPadUiState,
    onNumberTyped: (Int) -> Unit,
    onModifierToggled: (PerDartNumberPad.Modifier) -> Unit,
    clearNumberPad: () -> Unit,
    onEnterClicked: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(380.dp)) {
        if (numberPadUiState.numberPad is PerDartNumberPad) {
            val modifier by numberPadUiState.numberPad.modifier.collectAsState()

            PerDartNumPad(
                onNumberClicked = onNumberTyped,
                doubleModifierEnabled = modifier == PerDartNumberPad.Modifier.Double,
                onDoubleModifierClicked = { onModifierToggled(PerDartNumberPad.Modifier.Double) },
                tripleModifierEnabled = modifier == PerDartNumberPad.Modifier.Triple,
                onTripleModifierClicked = { onModifierToggled(PerDartNumberPad.Modifier.Triple) },
                disabledNumbers = numberPadUiState.disabledNumbers
            )
        } else {
            PerServeNumPad(
                onDigitClicked = onNumberTyped,
                onClearClicked = clearNumberPad,
                onEnterClicked = onEnterClicked,
                enterDisabled = !numberPadUiState.enterEnabled
            )
        }
    }
}

@Composable
private fun CheckoutInfo(
    checkoutTip: String?
) {
    if (checkoutTip == null) {
        return
    }
    MyCard() {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Checkout: $checkoutTip",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NumPadInfoAndActionsRow(
    onUndoClicked: () -> Unit,
    numberState: State<Int>,
    isEnterDisabled: Boolean,
    onSwapNumberPadClicked: () -> Unit,
    dartOrServeEnteredFlow: Flow<Int>,
) {
    // Shake animation
    var wasEnterDisabledPreviously = remember { false }
    var targetXOffset by remember { mutableStateOf(0.0) }

    val animatedXOffset: Dp by animateDpAsState(targetValue = targetXOffset.dp)

    LaunchedEffect(key1 = wasEnterDisabledPreviously == isEnterDisabled) {
        targetXOffset = 0.0
        wasEnterDisabledPreviously = isEnterDisabled
        if (!isEnterDisabled) {
            return@LaunchedEffect
        }
        delay(100)
        val durations = listOf(50, 110, 120, 130, 140)
        val offsets = listOf(10, -25, 25, -20, 10)
        for (i in durations.indices) {
            targetXOffset = offsets[i] * 0.4
            delay(durations[i].toLong())
        }
        targetXOffset = 0.0
    }

    // Enter animation
    class IndexedNumberHolder(var number: Int, val index: Int) {
        fun next() : IndexedNumberHolder {
            return IndexedNumberHolder(0, this.index + 1)
        }
    }
    val number by numberState
    var indexedNumberHolder by remember { mutableStateOf(IndexedNumberHolder(number, 0)) }
    indexedNumberHolder.number = number

    LaunchedEffect(key1 = true) {
        dartOrServeEnteredFlow.collect {
            indexedNumberHolder.number = it
            indexedNumberHolder = indexedNumberHolder.next()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SmallIconButton(
            icon = Icons.Default.Undo,
            onClick = onUndoClicked
        )

        AnimatedContent(
            targetState = indexedNumberHolder,
            transitionSpec = {
                (fadeIn() with
                        slideOutVertically() + fadeOut())
                    .using(
                        SizeTransform(clip = false)
                    )
            },
            modifier = Modifier.weight(1f)
        ) { targetIndexedNumberHolder ->
            Text(
                text = "${targetIndexedNumberHolder.number}",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(x = animatedXOffset)
            )
        }

        SmallIconButton(
            icon = Icons.Default.AppRegistration,
            onClick = onSwapNumberPadClicked
        )
    }
}

@Composable
private fun SmallIconButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 4.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(pressedElevation = 0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Change NumPad Layout",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DialogsOverlay(
    dialogUiState: DialogUiState,
    gameViewModel: GameViewModel
) {
    val legFinished by gameViewModel.legFinished.observeAsState(false)

    // The upper dialogs are hidden behind the dialogs listed later

    if (legFinished) {
        LegFinishedDialogEntryPoint(
            viewModel = gameViewModel.createLegFinishedDialogViewModel(),
            onPlayAgainClicked = gameViewModel::onPlayAgainClicked,
            onMenuClicked = {
                gameViewModel.dismissLegFinishedDialog()
                gameViewModel.navigateBack()
            }
        )
    }

    val doubleAttempts = dialogUiState.doubleAttemptsDialogOpen
    val checkout = dialogUiState.checkoutDialogOpen
    if (doubleAttempts || checkout) {
        DoubleAttemptsAndCheckoutDialog(
            askForDoubleAttempts = doubleAttempts,
            askForCheckout = checkout,
            minDartCount = gameViewModel.getMinimumDartCount(),
            onDialogCancelled = gameViewModel::onDoubleAttemptsAndCheckoutCancelled,
            onDialogConfirmed = gameViewModel::doubleAttemptsAndCheckoutConfirmed
        )
    }

    if (dialogUiState.simpleDoubleAttemptsDialogOpen) {
        SimpleDoubleAttemptsDialog(
            onAttemptClicked = gameViewModel::simpleDoubleAttemptsEntered
        )
    }

    if (dialogUiState.exitDialogOpen) {
        ExitDialog(
            onDismissDialog = gameViewModel::dismissExitDialog) {
            gameViewModel.dismissExitDialog()
            gameViewModel.navigateBack()
        }
    }
}



@Preview(widthDp = 380, heightDp = 780)
@Composable
private fun GameScreenPreview() {
    DartAppTheme() {
        val viewModel = GameViewModel(NavigationManager(), SettingsRepository(InMemoryKeyValueStorage()),
            FakeFinishedLegDao()
        )
        GameScreen(viewModel)
    }
}