package com.example.dartapp.util

import android.content.Context
import androidx.annotation.StringRes

object Strings {
    const val EMPTY_STRING = ""

    fun get(@StringRes stringRes: Int, context: Context = App.instance, vararg formatArgs: Any = emptyArray()): String {
        return context.getString(stringRes, *formatArgs)
    }

    fun getStringByIdName(context: Context, name: String): String {
        val res = context.resources
        return res.getString(res.getIdentifier(name, "string", context.packageName))
    }
}