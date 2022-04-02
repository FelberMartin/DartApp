package com.example.dartapp.util

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

import androidx.navigation.NavOptions
import com.example.dartapp.R
import com.example.dartapp.views.chart.util.DataPoint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


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

fun getDefaultNavOptions(): NavOptions? {
    return getDefaultNavOptionsBuilder().build()
}

fun getDefaultNavOptionsBuilder(): NavOptions.Builder {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
}

fun Date.weekDay() : String {
    return SimpleDateFormat("EE").format(this)
}

fun Date.timeString() : String {
    return SimpleDateFormat("HH:mm").format(this)
}

fun Date.dateString() : String {
    return SimpleDateFormat.getDateInstance().format(this)
}

private val decimalFormat = DecimalFormat("#.##")

fun decimalToString(n: Number) : String {
    val epsilon = 1e-6
    if (n.toFloat() < epsilon && n.toFloat() > - epsilon)
        return "0"
    return decimalFormat.format(n)
}

fun milliToDurationString(millis: Long, unitCount: Int = 2): String {
    val units = listOf("d", "h", "m", "s")
    val unitsInMilli = listOf(
        24 * 60 * 60 * 1000,
        60 * 60 * 1000,
        60 * 1000,
        1000
    )

    var valuesPerUnit = arrayListOf<Long>()
    for (i in units.indices) {
        var v = millis / unitsInMilli[i]
        if (i > 0) v %= unitsInMilli[i - 1] / unitsInMilli[i]
        valuesPerUnit.add(v)
    }

    var s = ""
    var unitsUsed = 0
    for (i in units.indices) {
        if (unitsUsed >= unitCount) break

        val v = valuesPerUnit[i]
        if (v == 0L) continue

        s += " $v${units[i]}"
        unitsUsed++
    }

    return s.trim()
}


