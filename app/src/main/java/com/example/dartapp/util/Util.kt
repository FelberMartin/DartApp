package com.example.dartapp.util

import android.content.Context

fun getStringByIdName(context: Context, name: String): String {
    val res = context.resources
    return res.getString(res.getIdentifier(name, "string", context.packageName))
}