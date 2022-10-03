package com.example.dartapp.views.chart.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

const val PURPLE_HEX = 0xFF8446CC
const val GREEN_HEX = 0xFF64B678
const val BLUE_HEX = 0xFF478AEA
const val YELLOW_HEX = 0xFFFDB54E
const val ORANGE_HEX = 0xFFF97C3C

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
            Color(PURPLE_HEX).toArgb(),
            Color(GREEN_HEX).toArgb(),
            Color(BLUE_HEX).toArgb(),
            Color(YELLOW_HEX).toArgb(),
            Color(ORANGE_HEX).toArgb()
        )

        val default = ColorManager(
            graphColors = defaultGraphColors,
            coordinateSystem = Color(0xFF343536).toArgb(),
            grid = Color(0xFFA8AAB3).toArgb(),
            selectionHighlighter = Color(0xFFB8BFCE).toArgb(),
            selectionLabelTitle = Color(0xFFFFFFFF).toArgb(),
            selectionLabelDescription = Color(0xFF939599).toArgb(),
            selectionLabelBackground = Color(0x9A000000).toArgb(),
            legendText = Color(0xFF000000).toArgb(),
        )

        @Composable
        fun materialThemeBasedColorManager() : ColorManager {
            val dark = isDarkTheme()
            return ColorManager(
                graphColors = defaultGraphColors,
                coordinateSystem = MaterialTheme.colorScheme.onSurfaceVariant.toArgb(),
                grid = MaterialTheme.colorScheme.outline.toArgb(),
                selectionHighlighter = MaterialTheme.colorScheme.surfaceVariant.toArgb(),
                selectionLabelTitle = MaterialTheme.colorScheme.inverseOnSurface.toArgb(),
                selectionLabelDescription = MaterialTheme.colorScheme.surfaceVariant.toArgb(),
                selectionLabelBackground = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.7f).toArgb(),
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