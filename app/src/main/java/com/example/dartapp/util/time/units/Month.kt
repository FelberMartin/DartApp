package com.example.dartapp.util.time.units

import com.example.dartapp.util.time.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

object Month : TimeUnit() {

    override fun toUiString(milliseconds: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val monthDate = SimpleDateFormat("MMM")
        return monthDate.format(cal.time)
    }

    override fun toIndex(milliseconds: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return month + year * 12
    }

    override fun toStartOfUnit(milliseconds: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val today = Day.toStartOfUnit(milliseconds)
        return today - cal.get(Calendar.DAY_OF_MONTH) * Day.MILLISECONDS_PER_DAY
    }

    override fun millisAfterGoingBack(unitCount: Int, milliseconds: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val firstDayOfMonth = toStartOfUnit(milliseconds)
        cal.timeInMillis = firstDayOfMonth
        cal.add(Calendar.MONTH, -unitCount)
        return cal.timeInMillis
    }
}