package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp


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
    title: String,
    subtitle: String,
    selectionInfoBoxConfig: SelectionInfoBoxConfig = ChartDefaults.selectionInfoConfig()
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = selectionInfoBoxConfig.backgroundColor),
        modifier = Modifier.padding(selectionInfoBoxConfig.padding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(selectionInfoBoxConfig.spacing)
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