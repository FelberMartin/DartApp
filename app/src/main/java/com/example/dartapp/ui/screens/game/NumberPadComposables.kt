package com.example.dartapp.ui.screens.game

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
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
        OnlyNumberRow(numbers = listOf(7, 8, 9), onNumberClicked = onDigitClicked)
        OnlyNumberRow(numbers = listOf(4, 5, 6), onNumberClicked = onDigitClicked)
        OnlyNumberRow(numbers = listOf(1, 2, 3), onNumberClicked = onDigitClicked)

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
    disabledNumbers: List<Int>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OnlyNumberRow(numbers = listOf(16, 17, 18, 19, 20), onNumberClicked = onNumberClicked,
            disabledNumbers = disabledNumbers)
        OnlyNumberRow(numbers = listOf(11, 12, 13, 14, 15), onNumberClicked = onNumberClicked,
            disabledNumbers = disabledNumbers)
        OnlyNumberRow(numbers = listOf(6, 7, 8, 9, 10), onNumberClicked = onNumberClicked,
            disabledNumbers = disabledNumbers)
        OnlyNumberRow(numbers = listOf(1, 2, 3, 4, 5), onNumberClicked = onNumberClicked,
            disabledNumbers = disabledNumbers)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BullsEyeButton(onNumberClicked = onNumberClicked, enabled = !disabledNumbers.contains(25))
            ModifierButton(text = "2x", enabled = doubleModifierEnabled, onClicked = onDoubleModifierClicked)
            ModifierButton(text = "3x", enabled = tripleModifierEnabled, onClicked = onTripleModifierClicked)
        }
    }
}

@Composable
private fun ColumnScope.OnlyNumberRow(
    numbers: List<Int>,
    onNumberClicked: (Int) -> Unit,
    disabledNumbers: List<Int> = listOf()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (number in numbers) {
            NumberButton(onNumberClicked, number, enabled = !disabledNumbers.contains(number))
        }
    }
}

@Composable
private fun RowScope.NumberButton(
    onNumberClicked: (Int) -> Unit,
    number: Int,
    enabled: Boolean = true
) {
    NumberPadButton(
        onClick = { onNumberClicked(number) },
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(6.dp),
        enabled = enabled,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .alpha(if (enabled) 1f else 0.4f)
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
    NumberPadButton(
        onClick = onClearClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
    NumberPadButton(
        onClick = onEnterClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors(
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
    enabled: Boolean
) {
    NumberButton(
        onNumberClicked = onNumberClicked,
        number = 25,
        enabled = enabled
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

    NumberPadButton(
        onClick = onClicked,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = containerColor),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = if (enabled) 0.dp else 2.dp),
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

@Composable
fun NumberPadButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    elevation: ButtonElevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = ElevatedButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    elevation = elevation,
    colors = colors,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content
)


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
        PerDartNumPad({}, false, {}, true, {}, listOf(5, 12))
    }
}