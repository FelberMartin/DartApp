package com.example.dartapp.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.game.numberpad.NumberPadBase
import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.game.numberpad.PerServeNumberPad
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.navigation.command.NavigationCommand
import com.example.dartapp.ui.screens.game.dialog.*
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.ui.values.Padding


@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    BackHandler() {
        viewModel.closeClicked()
    }
    Background {
        Column(Modifier.fillMaxSize()) {
            TopRow(onCloseClicked = viewModel::closeClicked)
            Column(
                modifier = Modifier
                    .padding(Padding.MediumPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PlayerStats(viewModel)
                Spacer(Modifier.height(20.dp))
                BottomElements(viewModel)
            }
        }
    }

    DialogsOverlay(viewModel)
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
    viewModel: GameViewModel
) {
    val pointsLeft by viewModel.pointsLeft.observeAsState(0)
    val dartCount by viewModel.dartCount.observeAsState(0)
    val last by viewModel.last.observeAsState("")
    val average by viewModel.average.observeAsState("")

    MyCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .padding(12.dp)
        ) {
            Text(
                text = pointsLeft.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerGameStatistic(
                    name = "Darts: ",
                    valueString = dartCount.toString()
                )

                PlayerGameStatistic(
                    name = "Last: ",
                    valueString = last
                )

                PlayerGameStatistic(
                    name = "Ø ",
                    valueString = average
                )
            }
        }
    }
}

@Composable
private fun PlayerGameStatistic(
    name: String,
    valueString: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = valueString,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun BottomElements(
    viewModel: GameViewModel
) {
    val numberPad by viewModel.numberPad.observeAsState(PerServeNumberPad())

    Column() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CheckoutInfo(viewModel.checkoutTip.observeAsState().value)

            NumPadInfoAndActionsRow(
                onUndoClicked = viewModel::onUndoClicked,
                numberState = numberPad.number.collectAsState(),
                onSwapNumberPadClicked = viewModel::onSwapNumberPadClicked
            )

            PickNumberPadVersion(viewModel, numberPad)
        }
    }
}

@Composable
private fun PickNumberPadVersion(
    viewModel: GameViewModel,
    numberPad: NumberPadBase,
) {
    val enterDisabled by viewModel.enterDisabled.observeAsState(false)

    Column(
        Modifier
            .fillMaxWidth()
            .height(380.dp)) {
        if (viewModel.usePerDartNumberPad && numberPad is PerDartNumberPad) {
            val perDartNumberPad = numberPad as PerDartNumberPad
            val modifier by perDartNumberPad.modifier.collectAsState()

            PerDartNumPad(
                onNumberClicked = viewModel::onNumberTyped,
                doubleModifierEnabled = modifier == PerDartNumberPad.Modifier.Double,
                onDoubleModifierClicked = { viewModel.onModifierToggled(PerDartNumberPad.Modifier.Double) },
                tripleModifierEnabled = modifier == PerDartNumberPad.Modifier.Triple,
                onTripleModifierClicked = { viewModel.onModifierToggled(PerDartNumberPad.Modifier.Triple) },
                disabledNumbers = viewModel.getDisabledNumbers(modifier)
            )
        } else {
            PerServeNumPad(
                onDigitClicked = viewModel::onNumberTyped,
                onClearClicked = viewModel::clearNumberPad,
                onEnterClicked = viewModel::onEnterClicked,
                enterDisabled = enterDisabled
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
    onSwapNumberPadClicked: () -> Unit
) {
    val number by numberState

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SmallIconButton(
            icon = Icons.Default.Undo,
            onClick = onUndoClicked
        )

        Text(
            text = "$number",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.secondary
        )

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
private fun DialogsOverlay(viewModel: GameViewModel) {
    val uiState by viewModel.dialogUiState.observeAsState(DialogUiState())
    val legFinished by viewModel.legFinished.observeAsState(false)

    // The upper dialogs are hidden behind the dialogs listed later

    if (legFinished) {
        LegFinishedDialogEntryPoint(
            viewModel = viewModel.createLegFinishedDialogViewModel(),
            onPlayAgainClicked = viewModel::onPlayAgainClicked,
            onMenuClicked = { viewModel.navigate(NavigationCommand.NAVIGATE_UP) }
        )
    }

    val doubleAttempts = uiState.doubleAttemptsDialogOpen
    val checkout = uiState.checkoutDialogOpen
    if (doubleAttempts || checkout) {
        DoubleAttemptsAndCheckoutDialog(
            askForDoubleAttempts = doubleAttempts,
            askForCheckout = checkout,
            minDartCount = viewModel.getMinimumDartCount(),
            onDialogCancelled = viewModel::onDoubleAttemptsAndCheckoutCancelled,
            onDialogConfirmed = viewModel::doubleAttemptsAndCheckoutConfirmed
        )
    }

    if (uiState.simpleDoubleAttemptsDialogOpen) {
        SimpleDoubleAttemptsDialog(
            onAttemptClicked = viewModel::simpleDoubleAttemptsEntered
        )
    }

    if (uiState.exitDialogOpen) {
        ExitDialog(
            onDismissDialog = viewModel::dismissExitDialog) {
            viewModel.dismissExitDialog()
            viewModel.navigate(NavigationCommand.NAVIGATE_UP)
        }
    }
}



@Preview(widthDp = 380, heightDp = 780)
@Composable
private fun GameScreenPreview() {
    DartAppTheme() {
        val viewModel = GameViewModel(NavigationManager(), SettingsRepository(InMemoryKeyValueStorage()),
            FakeLegDatabaseDao())
        GameScreen(viewModel)
    }
}