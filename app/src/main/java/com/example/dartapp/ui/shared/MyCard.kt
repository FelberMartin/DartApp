@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.outlinedCardColors(
        containerColor = Color.White
    ),
    content: @Composable ColumnScope.() -> Unit
) = OutlinedCard(
    modifier = modifier,
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
    colors = colors,
    content = content
)

@Composable
fun MyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    content: @Composable ColumnScope.() -> Unit
) = OutlinedCard(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    interactionSource = interactionSource,
    border = CardDefaults.outlinedCardBorder(enabled),
    elevation = CardDefaults.elevatedCardElevation(),
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