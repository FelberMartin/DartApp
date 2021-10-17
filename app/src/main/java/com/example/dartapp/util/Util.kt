package com.example.dartapp.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import android.R

import androidx.navigation.NavOptions




object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }
}

object Colors {
    fun get(@ColorRes colorRes: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            App.instance.getColor(colorRes)
        } else {
            return Color.MAGENTA
        }
    }
}


fun getStringByIdName(context: Context, name: String): String {
    val res = context.resources
    return res.getString(res.getIdentifier(name, "string", context.packageName))
}

fun getNavOptions(): NavOptions? {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_left)
        .setExitAnim(R.anim.slide_out_right)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
}
