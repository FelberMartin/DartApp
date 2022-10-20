package com.development_felber.dartapp.chartlibrary.compose

import androidx.compose.ui.graphics.Color

data class ColorSequence(
    val colors: List<Color>
) {

    fun getColor(index: Int) : Color {
        return colors[index % colors.size]
    }

    companion object {
        val default = ColorSequence(listOf(
            Color(0xFF8446CC),
            Color(0xFF64B678),
            Color(0xFF478AEA),
            Color(0xFFFDB54E),
            Color(0xFFF97C3C)
        ))

        val lightDartApp = ColorSequence(listOf(
            Color(0xFF515DEB),
            Color(0xFF64B678),
            Color(0xFFFDB54E),
            Color(0xFFF97C3C),
            Color(0xFF9B3AB1),
        ))

        val darkDartApp = ColorSequence(listOf(
            Color(0xFF7078d2),
            Color(0xFF81C784),
            Color(0xFFFFF176),
            Color(0xFFFF8A65),
            Color(0xFFBA68C8),
        ))
    }
}