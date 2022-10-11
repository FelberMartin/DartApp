package com.development_felber.dartapp.views.chart.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.development_felber.dartapp.ui.theme.isDarkTheme

data class ColorManager(
    private val graphColors: List<Int>,
    val coordinateSystem: Int,
    val grid: Int,
    val selectionHighlighter: Int,
    val selectionLabelTitle: Int,
    val selectionLabelDescription: Int,
    val selectionLabelBackground: Int,
    val legendText: Int,
    var onlyUseOneGraphColor: Boolean = false
){

    fun getGraphColor(index: Int) : Int {
        if (onlyUseOneGraphColor) {
            return graphColors[0]
        }
        return graphColors[index % graphColors.size]
    }

    companion object {
        val defaultGraphColors = listOf<Int>(
            Color(0xFF8446CC).toArgb(),
            Color(0xFF64B678).toArgb(),
            Color(0xFF478AEA).toArgb(),
            Color(0xFFFDB54E).toArgb(),
            Color(0xFFF97C3C).toArgb()
        )

        val lightGraphColors = listOf<Int>(
            Color(0xFF515DEB).toArgb(),
            Color(0xFF64B678).toArgb(),
            Color(0xFFFDB54E).toArgb(),
            Color(0xFFF97C3C).toArgb(),
            Color(0xFF9B3AB1).toArgb(),
        )

        val darkGraphColors = listOf<Int>(
            Color(0xFF7078d2).toArgb(),
            Color(0xFF81C784).toArgb(),
            Color(0xFFFFF176).toArgb(),
            Color(0xFFFF8A65).toArgb(),
            Color(0xFFBA68C8).toArgb(),
        )

        val default = ColorManager(
            graphColors = defaultGraphColors,
            coordinateSystem = Color(0xFF343536).toArgb(),
            grid = Color(0xFFA8AAB3).toArgb(),
            selectionHighlighter = Color(0xFFB8BFCE).toArgb(),
            selectionLabelTitle = Color(0xFFFFFFFF).toArgb(),
            selectionLabelDescription = Color(0xFFB7C0CC).toArgb(),
            selectionLabelBackground = Color(0x9A000000).toArgb(),
            legendText = Color(0xFF000000).toArgb(),
        )

        @Composable
        fun materialThemeBasedColorManager() : ColorManager {
            return ColorManager(
                graphColors = if (MaterialTheme.isDarkTheme()) darkGraphColors else lightGraphColors,
                coordinateSystem = MaterialTheme.colorScheme.onSurfaceVariant.toArgb(),
                grid = MaterialTheme.colorScheme.outline.toArgb(),
                selectionHighlighter = MaterialTheme.colorScheme.surfaceVariant.toArgb(),
                selectionLabelTitle = default.selectionLabelTitle,
                selectionLabelDescription = default.selectionLabelDescription,
                selectionLabelBackground = default.selectionLabelBackground,
                legendText = MaterialTheme.colorScheme.onBackground.toArgb(),
            )
        }
    }

}