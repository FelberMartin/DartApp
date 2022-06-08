package com.example.dartapp.util.time.units

import com.example.dartapp.util.time.TimeUnit
import com.example.dartapp.util.weekDayString
import java.util.*

object Day : TimeUnit() {
    const val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L
    override fun toUiString(milliseconds: Long): String {
        return Date(milliseconds).weekDayString()
    }

    override fun toIndex(milliseconds: Long): Int {
        return (milliseconds / MILLISECONDS_PER_DAY).toInt()
    }

    override fun toStartOfUnit(milliseconds: Long): Long {
        return milliseconds - (milliseconds % MILLISECONDS_PER_DAY)
    }

    override fun millisAfterGoingBack(unitCount: Int, milliseconds: Long): Long {
        val today = toStartOfUnit(milliseconds)
        return today - unitCount * MILLISECONDS_PER_DAY
    }
}