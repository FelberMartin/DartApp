@file:OptIn(ExperimentalComposeUiApi::class)

package com.development_felber.dartapp.ui.screens.game.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.development_felber.dartapp.ui.shared.MyCard
import com.development_felber.dartapp.ui.theme.DartAppTheme


@Composable
fun GameFinishedDialog(
    title: String,
    onMoreDetailsClicked: () -> Unit,
    moreDetailsEnabled: Boolean = true,
    onMenuClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit,
    showStatisticsContent: Boolean = true,
    statisticsContent: @Composable () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.85f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { },
        title = {
            Text(title)
        },
        text = {
            Box(Modifier.animateContentSize()) {
                if (showStatisticsContent) {
                    StatisticsSection(
                        onMoreDetailsClicked = onMoreDetailsClicked,
                        moreDetailsEnabled = moreDetailsEnabled,
                    ) {
                        statisticsContent()
                    }
                } else {
                    Text("Would you like to play again?")
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onMenuClicked) {
                    Text("Back to Menu")
                }

                Spacer(Modifier.width(12.dp))

                Button(onClick = onPlayAgainClicked) {
                    Text(
                        text = "Play again",
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    )
}

@Composable
private fun StatisticsSection(
    onMoreDetailsClicked: () -> Unit,
    moreDetailsEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    MyCard(modifier = Modifier.padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            content()

            Spacer(modifier = Modifier.height(30.dp))

            MoreDetailsButton(
                onClick = onMoreDetailsClicked,
                enabled = moreDetailsEnabled,
            )
        }
    }
}

@Composable
private fun MoreDetailsButton(
    enabled: Boolean,
    onClick: (() -> Unit)
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            imageVector = Icons.Filled.StackedLineChart,
            contentDescription = null
        )
        Text(
            text = "more Details",
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameFinishedDialogPreview() {
    DartAppTheme() {
        GameFinishedDialog(
            title = "Title",
            onMoreDetailsClicked = { /*TODO*/ },
            onMenuClicked = { /*TODO*/ },
            onPlayAgainClicked = { /*TODO*/ }) {
            Text("Content")
        }
    }
}