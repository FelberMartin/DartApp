package com.example.dartapp.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

import androidx.navigation.NavOptions
import com.example.dartapp.R


object Strings {
    fun get(@StringRes stringRes: Int, context: Context = App.instance, vararg formatArgs: Any = emptyArray()): String {
        return context.getString(stringRes, *formatArgs)
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
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
}
