package com.example.dartapp.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Row(Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1f))

        IconButton(onClick = onCloseClicked) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = pointsLeft.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 20.dp)
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
        if (viewModel.usePerDartNumberPad) {
            val perDartNumberPad = numberPad as PerDartNumberPad
            val doubleEnabled by perDartNumberPad.doubleModifierEnabled.collectAsState()
            val tripleEnabled by perDartNumberPad.tripleModifierEnabled.collectAsState()

            PerDartNumPad(
                onNumberClicked = viewModel::onNumberTyped,
                doubleModifierEnabled = doubleEnabled,
                onDoubleModifierClicked = viewModel::onDoubleModifierToggled,
                tripleModifierEnabled = tripleEnabled,
                onTripleModifierClicked = viewModel::onTripleModifierToggled,
                onEnterClicked = viewModel::onEnterClicked,
                enterDisabled = enterDisabled
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
        IconButton(
            onClick = onUndoClicked,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo"
            )
        }

        Text(
            text = "$number",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        IconButton(
            onClick = onSwapNumberPadClicked,
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.AppRegistration,
                contentDescription = "Change NumPad Layout"
            )
        }
    }
}

@Composable
private fun DialogsOverlay(viewModel: GameViewModel) {
    val uiState by viewModel.dialogUiState.observeAsState(DialogUiState())
    val legFinished by viewModel.legFinished.observeAsState(false)

    // The upper dialogs are hidden behind the dialogs listed later

    if (legFinished) {
        LegFinishedDialog(
            onPlayAgainClicked = viewModel::onPlayAgainClicked,
            onMenuClicked = { viewModel.navigate(NavigationCommand.NAVIGATE_UP) }
        )
    }

    if (uiState.checkoutDialogOpen) {
        CheckoutDialog(
            minimumRequiredDarts = viewModel.getLastDoubleAttempts(),
            onNumberClicked = viewModel::checkoutDartsEntered
        )
    }

    if (uiState.doubleAttemptsDialogOpen) {
        DoubleAttemptsDialog(
            onNumberClicked = viewModel::doubleAttemptsEntered
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