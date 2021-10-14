package com.example.dartapp.views.chart.util

import android.graphics.Color
import com.example.dartapp.R
import com.example.dartapp.util.App
import com.example.dartapp.util.Colors

class ColorManager {

    var list = listOf<Int>(
        Color.parseColor("#FF8446CC"),
        Color.parseColor("#FF64B678"),
        Color.parseColor("#FF478AEA"),
        Color.parseColor("#FFFDB54E"),
        Color.parseColor("#FFF97C3C")
    )

    fun get(index: Int) : Int {
        return list[index % list.size]
    }

}