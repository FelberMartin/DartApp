package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme
import kotlin.math.ceil

data class LegendEntry(
    val color: Color,
    val text: String
)

@Composable
fun Legend(
    legendEntries: List<LegendEntry>,
    maxEntriesPerRow: Int = Int.MAX_VALUE,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    indicatorShape: Shape = MaterialTheme.shapes.extraLarge,
    indicatorSize: Dp = 12.dp,
    legendEntryIndicatorTextSpacing: Dp = 4.dp,
    entriesSpacing: Dp = 12.dp
) {
    val rowCount = ceil(legendEntries.size.toDouble() / maxEntriesPerRow).toInt()

    Row(
        horizontalArrangement = Arrangement.spacedBy(entriesSpacing)
    ) {
        for (columnIndex in 0 until maxEntriesPerRow) {
            Column() {
                for (rowIndex in 0 until rowCount) {
                    val entriesIndex = rowIndex * maxEntriesPerRow + columnIndex
                    if (entriesIndex >= legendEntries.size) {
                        break
                    }
                    LegendEntryComposable(
                        legendEntry = legendEntries[entriesIndex],
                        textStyle = textStyle,
                        indicatorShape = indicatorShape,
                        indicatorSize = indicatorSize,
                        spacing = legendEntryIndicatorTextSpacing
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendEntryComposable(
    legendEntry: LegendEntry,
    textStyle: TextStyle,
    indicatorShape: Shape,
    indicatorSize: Dp,
    spacing: Dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(indicatorSize)
                .clip(indicatorShape)
                .background(color = legendEntry.color)
        )

        Spacer(Modifier.width(spacing))
        Text(
            text = legendEntry.text,
            style = textStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LegendEntryComposablePreview() {
    DartAppTheme {
        LegendEntryComposable(
            legendEntry = LegendEntry(color = Color.Red, text = "Entry of Doom"),
            textStyle = MaterialTheme.typography.labelLarge,
            indicatorShape = MaterialTheme.shapes.extraLarge,
            indicatorSize = 16.dp,
            spacing = 12.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LegendPreview() {
    DartAppTheme {
        Legend(
            legendEntries = listOf(
                LegendEntry(color = Color.Red, text = "Entry of Doom"),
                LegendEntry(color = Color.Blue, text = "Elephants"),
                LegendEntry(color = Color.Yellow, text = "Blauwals"),
                LegendEntry(color = Color.Green, text = "Obst im Haus"),
                LegendEntry(color = Color.Magenta, text = "Slightly longer text here")
            ),
            maxEntriesPerRow = 2
        )
    }
}