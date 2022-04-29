package com.example.dartapp.util

import androidx.navigation.NavOptions
import com.example.dartapp.R

object Navigation {

    fun getDefaultNavOptions(): NavOptions {
        return getDefaultNavOptionsBuilder().build()
    }

    fun getDefaultNavOptionsBuilder(): NavOptions.Builder {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
    }
}