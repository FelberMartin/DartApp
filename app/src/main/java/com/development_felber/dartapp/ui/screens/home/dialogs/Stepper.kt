package com.development_felber.dartapp.ui.screens.home.dialogs

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

@OptIn(ExperimentalAnimationApi::class)
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
            enabled = value > minValue,
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

        AnimatedContent(
            targetState = value,
            transitionSpec = {
                // Compare the incoming number with the previous number.
                if (targetState > initialState) {
                    // If the target number is larger, it slides up and fades in
                    // while the initial (smaller) number slides up and fades out.
                    slideInVertically { height -> height } + fadeIn() with
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    // If the target number is smaller, it slides down and fades in
                    // while the initial number slides down and fades out.
                    slideInVertically { height -> -height } + fadeIn() with
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )
            }
        ) { targetCount ->
            Text(
                text = "$targetCount",
                style = MaterialTheme.typography.headlineSmall,
            )
        }


        StepperButton(
            enabled = value < maxValue,
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
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(36.dp),
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