package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.isDarkTheme


object ChartDefaults {

    @Composable
    fun selectionInfoConfig(
        titleStyle: TextStyle = MaterialTheme.typography.labelLarge,
        titleColor: Color = Color.White,
        subtitleStyle: TextStyle = MaterialTheme.typography.labelMedium,
        subtitleColor: Color = Color.LightGray,
        padding: PaddingValues = PaddingValues(8.dp),
        spacing: Dp = 4.dp,
        backgroundColor: Color = Color.Black.copy(alpha = 0.7f)
    ) = SelectionInfoBoxConfig(
        titleStyle = titleStyle,
        titleColor = titleColor,
        subtitleColor = subtitleColor,
        subtitleStyle = subtitleStyle,
        padding = padding,
        spacing = spacing,
        backgroundColor = backgroundColor
    )

    @Composable
    fun colorSequence(darkTheme: Boolean = MaterialTheme.isDarkTheme()) : ColorSequence {
        if (darkTheme) {
            return ColorSequence.darkDartApp
        }
        return ColorSequence.lightDartApp
    }

}