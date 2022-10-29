package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.development_felber.dartapp.util.extensions.translated


data class SelectionInfoBoxConfig(
    val titleStyle: TextStyle,
    val titleColor: Color,
    val subtitleStyle: TextStyle,
    val subtitleColor: Color,
    val padding: PaddingValues,
    val spacing: Dp,
    val backgroundColor: Color,
)

@Composable
fun SelectionInfoBox(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig()
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = selectionInfoBoxConfig.backgroundColor),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(selectionInfoBoxConfig.spacing),
            modifier = Modifier.padding(selectionInfoBoxConfig.padding)
        ) {
            Text(
                text = title,
                style = selectionInfoBoxConfig.titleStyle,
                color = selectionInfoBoxConfig.titleColor
            )
            Text (
                text = subtitle,
                style = selectionInfoBoxConfig.subtitleStyle,
                color = selectionInfoBoxConfig.subtitleColor
            )
        }
    }
}


@Composable
fun SelectionInfoBox(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig(),
    middleOffset: IntOffset,
) {
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    SelectionInfoBox(
        title = title,
        subtitle = subtitle,
        selectionInfoBoxConfig = selectionInfoBoxConfig,
        modifier = Modifier
            .offset {
                middleOffset.minus(IntOffset(size.width / 2, size.height / 2))
            }
            .onSizeChanged {
                size = it
            }
            .then(modifier)
    )
}

@Preview(showBackground = true)
@Composable
fun SelectionInfoBoxPreview() {
    DartAppTheme() {
        SelectionInfoBox(title = "Title", subtitle = "Subtitle")
    }
}