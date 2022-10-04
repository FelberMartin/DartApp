package com.example.dartapp.views.chart.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

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
            val dark = isDarkTheme()
            return ColorManager(
                graphColors = lightGraphColors,
                coordinateSystem = MaterialTheme.colorScheme.onSurfaceVariant.toArgb(),
                grid = MaterialTheme.colorScheme.outline.toArgb(),
                selectionHighlighter = MaterialTheme.colorScheme.surfaceVariant.toArgb(),
                selectionLabelTitle = default.selectionLabelTitle,
                selectionLabelDescription = default.selectionLabelDescription,
                selectionLabelBackground = default.selectionLabelBackground,
                legendText = MaterialTheme.colorScheme.onBackground.toArgb(),
            )
        }

        @Composable
        private fun isDarkTheme() : Boolean {
            val background = MaterialTheme.colorScheme.background
            val brightness = background.red + background.green + background.blue
            return brightness < 1.5f
        }
    }

}