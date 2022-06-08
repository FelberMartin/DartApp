package com.example.dartapp.util

import com.example.dartapp.util.resources.Strings
import java.text.DecimalFormat

object NumberFormatter {

    private val decimalFormat = DecimalFormat("#.##")
    private const val EPSILON = 1e-6


    private enum class TimeUnit(val abbreviation: String, val millisPerUnit: Long) {
        DAY("d", 24 * 60 * 60 * 1000),
        HOUR("h", 60 * 60 * 1000),
        MINUTE("m", 60 * 1000),
        SECOND("s", 1000);

        companion object {
            fun ordered(): List<TimeUnit> {
                return listOf(DAY, HOUR, MINUTE, SECOND)
            }
        }
    }

    fun decimalToString(n: Number) : String {
        if (n.toFloat() < EPSILON && n.toFloat() > - EPSILON)
            return "0"
        return decimalFormat.format(n)
    }

    fun milliToDurationString(millis: Long, maxUnitCount: Int = 2): String {
        var s = ""
        var unitsUsed = 0
        for ((index, timeUnit) in TimeUnit.ordered().withIndex()) {
            if (unitsUsed >= maxUnitCount) break

            var unitCount = millis / timeUnit.millisPerUnit
            if (index > 0) {
                val stepSizeToBiggerUnit = TimeUnit.ordered()[index - 1].millisPerUnit / timeUnit.millisPerUnit
                unitCount %= stepSizeToBiggerUnit
            }

            if (unitCount == 0L) continue

            s += " $unitCount${timeUnit.abbreviation}"
            unitsUsed++
        }

        if (s == Strings.EMPTY_STRING) {
            s = "0${TimeUnit.ordered().last().abbreviation}"
        }

        return s.trim()
    }
}



