package com.example.dartapp.util.time.units

import com.example.dartapp.util.time.TimeUnit
import java.time.LocalDateTime
import java.time.temporal.WeekFields

object Week : TimeUnit(
    uniquePattern = "",
    uiPattern = ""
) {

    private fun weekInYear(dateTime: LocalDateTime): Int {
        return dateTime.get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    override fun toUiString(dateTime: LocalDateTime): String {
        return "CW${weekInYear(dateTime)}"
    }

    override fun toUniqueString(dateTime: LocalDateTime): String {
        return "CW${weekInYear(dateTime)}(${dateTime.year})"
    }

    override fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime): LocalDateTime {
        return dateTime.minusWeeks(unitCount.toLong())
    }
}