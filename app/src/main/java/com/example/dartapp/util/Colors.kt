package com.example.dartapp.util

import androidx.annotation.ColorRes

object Colors {
    fun get(@ColorRes colorRes: Int): Int {
        return App.instance.getColor(colorRes)
    }
}