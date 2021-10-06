package com.example.dartapp.views.chart

import android.graphics.Color

class ColorManager {

    var list = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN,
        Color.MAGENTA, Color.YELLOW)

    fun get(index: Int) : Int {
        return list[index]
    }

}