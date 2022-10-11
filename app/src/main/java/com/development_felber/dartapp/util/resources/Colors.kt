package com.development_felber.dartapp.util.resources

import androidx.annotation.ColorRes
import com.development_felber.dartapp.util.App

object Colors {
    fun get(@ColorRes colorRes: Int): Int {
        return App.instance.applicationContext.getColor(colorRes)
    }
}