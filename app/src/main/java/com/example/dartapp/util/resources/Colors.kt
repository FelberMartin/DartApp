package com.example.dartapp.util.resources

import androidx.annotation.ColorRes
import com.example.dartapp.util.App

object Colors {
    fun get(@ColorRes colorRes: Int): Int {
        return App.instance.applicationContext.getColor(colorRes)
    }
}