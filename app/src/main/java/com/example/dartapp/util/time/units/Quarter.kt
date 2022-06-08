package com.example.dartapp.util.time.units

import com.example.dartapp.util.time.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

object Quarter : TimeUnit() {
    override fun toUiString(milliseconds: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val year = SimpleDateFormat("YY").format(cal.time)
        val quarterNumber = (toIndex(milliseconds) % 4) + 1
        return "Q$quarterNumber($year)"
    }

    override fun toIndex(milliseconds: Long): Int {
        return Month.toIndex(milliseconds) / 3
    }

    override fun toStartOfUnit(milliseconds: Long): Long {
        val monthIndexInQuarter = Month.toIndex(milliseconds) % 3
        return Month.millisAfterGoingBack(monthIndexInQuarter, milliseconds)
    }

    override fun millisAfterGoingBack(unitCount: Int, milliseconds: Long): Long {
        val startOfQuarter = toStartOfUnit(milliseconds)
        val cal = Calendar.getInstance()
        cal.timeInMillis = startOfQuarter
        cal.add(Calendar.MONTH, -3 * unitCount)
        return cal.timeInMillis
    }
}