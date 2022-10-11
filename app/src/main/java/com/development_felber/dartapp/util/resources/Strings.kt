package com.development_felber.dartapp.util.resources

import android.content.Context
import androidx.annotation.StringRes
import com.development_felber.dartapp.util.App

object Strings {
    const val EMPTY_STRING = ""

    fun get(@StringRes stringRes: Int, context: Context = App.instance.applicationContext, vararg formatArgs: Any = emptyArray()): String {
        return context.getString(stringRes, *formatArgs)
    }

    fun getStringByIdName(context: Context, name: String): String {
        val res = context.resources
        return res.getString(res.getIdentifier(name, "string", context.packageName))
    }
}