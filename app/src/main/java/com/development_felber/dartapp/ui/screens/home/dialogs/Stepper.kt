package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.filledTonalButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            onClick = {
                if (value > minValue) {
                    onValueChange(value - 1)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease value"
            )
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
        )

        StepperButton(
            onClick = {
                if (value < maxValue) {
                    onValueChange(value + 1)
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase value"
            )
        }
    }
}

@Composable
private fun StepperButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun StepperPreview() {
    DartAppTheme {
        Stepper(
            value = 3,
            onValueChange = { },
            minValue = 1,
            maxValue = 5
        )
    }
}