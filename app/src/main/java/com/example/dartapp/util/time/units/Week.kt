package com.example.dartapp.util.time.units

import com.example.dartapp.R
import com.example.dartapp.util.resources.Strings
import com.example.dartapp.util.time.TimeUnit
import java.util.*

object Week : TimeUnit() {
    /** 01.01.1970 was a Thursday. */
    private const val FIRST_COMPLETE_WEEK_OFFSET = 4 * Day.MILLISECONDS_PER_DAY
    const val MILLISECONDS_PER_WEEK = 7 * Day.MILLISECONDS_PER_DAY

    override fun toUiString(milliseconds: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        val week = cal.get(Calendar.WEEK_OF_YEAR) + 1
        val abbreviation = Strings.get(R.string.calendar_week_abr)
        return "$abbreviation$week"
    }

    override fun toIndex(milliseconds: Long): Int {
        return ((milliseconds - FIRST_COMPLETE_WEEK_OFFSET) % MILLISECONDS_PER_WEEK).toInt()
    }

    override fun toStartOfUnit(milliseconds: Long): Long {
        return milliseconds - ((milliseconds - FIRST_COMPLETE_WEEK_OFFSET) % MILLISECONDS_PER_WEEK)
    }

    override fun millisAfterGoingBack(unitCount: Int, milliseconds: Long): Long {
        val thisWeek = toStartOfUnit(milliseconds)
        return thisWeek - unitCount * MILLISECONDS_PER_WEEK
    }
}