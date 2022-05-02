package com.example.dartapp.views.chart.util

import android.graphics.Color

const val PURPLE_HEX = "#FF8446CC"
const val GREEN_HEX = "#FF64B678"
const val BLUE_HEX = "#FF478AEA"
const val YELLOW_HEX = "#FFFDB54E"
const val ORANGE_HEX = "#FFF97C3C"

class ColorManager private constructor(val colorList: List<Int>){

    fun get(index: Int) : Int {
        return this.colorList[index % this.colorList.size]
    }

    companion object {
        val default = ColorManager(listOf<Int>(
            Color.parseColor(PURPLE_HEX),
            Color.parseColor(GREEN_HEX),
            Color.parseColor(BLUE_HEX),
            Color.parseColor(YELLOW_HEX),
            Color.parseColor(ORANGE_HEX)
        ))

        val singleColor = ColorManager(listOf(Color.parseColor(PURPLE_HEX)))
    }

}