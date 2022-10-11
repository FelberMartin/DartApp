@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = ElevatedCard(
    modifier = modifier,
    elevation = CardDefaults.elevatedCardElevation(),
    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
    content = content
)

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    colors: CardColors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background),
    content: @Composable ColumnScope.() -> Unit
) = ElevatedCard(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    interactionSource = interactionSource,
    elevation = elevation,
    colors = colors,
    content = content
)

@Preview(showBackground = true, widthDp = 340, heightDp = 340)
@Composable
fun MyCardPreview() {
    Surface {
        MyCard(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {

        }
    }
}