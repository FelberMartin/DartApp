package com.example.dartapp.ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.ui.theme.DartAppTheme

@Composable
fun PerServeNumPad(
    onDigitClicked: (Int) -> Unit,
    onClearClicked: () -> Unit,
    onEnterClicked: () -> Unit,
    enterDisabled: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        onlyNumberRow(numbers = listOf(7, 8, 9), onNumberClicked = onDigitClicked)
        onlyNumberRow(numbers = listOf(4, 5, 6), onNumberClicked = onDigitClicked)
        onlyNumberRow(numbers = listOf(1, 2, 3), onNumberClicked = onDigitClicked)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClearButton(onClearClicked = onClearClicked)
            NumberButton(onNumberClicked = onDigitClicked, number = 0)
            EnterButton(onEnterClicked = onEnterClicked, disabled = enterDisabled)
        }
    }
}

@Composable
fun PerDartNumPad(
    onNumberClicked: (Int) -> Unit,
    doubleModifierEnabled: Boolean,
    onDoubleModifierClicked: () -> Unit,
    tripleModifierEnabled: Boolean,
    onTripleModifierClicked: () -> Unit,
    onEnterClicked: () -> Unit,
    enterDisabled: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        onlyNumberRow(numbers = listOf(16, 17, 18, 19, 20), onNumberClicked = onNumberClicked)
        onlyNumberRow(numbers = listOf(11, 12, 13, 14, 15), onNumberClicked = onNumberClicked)
        onlyNumberRow(numbers = listOf(6, 7, 8, 9, 10), onNumberClicked = onNumberClicked)
        onlyNumberRow(numbers = listOf(1, 2, 3, 4, 5), onNumberClicked = onNumberClicked)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BullsEyeButton(onNumberClicked = onNumberClicked, tripleModifierEnabled = tripleModifierEnabled)
            ModifierButton(text = "2x", enabled = doubleModifierEnabled, onClicked = onDoubleModifierClicked)
            ModifierButton(text = "3x", enabled = tripleModifierEnabled, onClicked = onTripleModifierClicked)
            EnterButton(onEnterClicked = onEnterClicked, disabled = enterDisabled)
        }
    }
}

@Composable
private fun ColumnScope.onlyNumberRow(
    numbers: List<Int>,
    onNumberClicked: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (number in numbers) {
            NumberButton(onNumberClicked, number)
        }
    }
}

@Composable
private fun RowScope.NumberButton(
    onNumberClicked: (Int) -> Unit,
    number: Int,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = { onNumberClicked(number) },
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(6.dp),
        enabled = enabled,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f).alpha(if (enabled) 1f else 0.4f)
    ) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun RowScope.ClearButton(
    onClearClicked: () -> Unit
) {
    OutlinedButton(
        onClick = onClearClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
    ) {
        Text(
            text = "C",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun RowScope.EnterButton(
    onEnterClicked: () -> Unit,
    disabled: Boolean = false
) {
    OutlinedButton(
        onClick = onEnterClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary
        ),
        enabled = !disabled,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Enter",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun RowScope.BullsEyeButton(
    onNumberClicked: (Int) -> Unit,
    tripleModifierEnabled: Boolean
) {
    NumberButton(
        onNumberClicked = onNumberClicked,
        number = 25,
        enabled = !tripleModifierEnabled
    )
}

@Composable
private fun RowScope.ModifierButton(
    text: String,
    enabled: Boolean,
    onClicked: () -> Unit
) {
    val containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
    val textColor = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary

    OutlinedButton(
        onClick = onClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor),
        contentPadding = PaddingValues(6.dp),
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = textColor
        )
    }
}


@Preview(showBackground = true, widthDp = 300, heightDp = 340)
@Composable
private fun previewPerServeNumPad() {
    DartAppTheme() {
        PerServeNumPad({}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 340)
@Composable
private fun previewPerDartNumPad() {
    DartAppTheme() {
        PerDartNumPad({}, false, {}, true, {}, {})
    }
}