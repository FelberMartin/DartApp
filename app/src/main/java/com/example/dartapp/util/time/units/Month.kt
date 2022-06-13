package com.example.dartapp.util.time.units

import android.icu.text.DateFormat
import com.example.dartapp.util.time.TimeUnit
import java.time.LocalDateTime

object Month : TimeUnit(
    uniquePattern = DateFormat.YEAR_NUM_MONTH,
    uiPattern = DateFormat.ABBR_MONTH
) {

    override fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime): LocalDateTime {
        return dateTime.minusMonths(unitCount.toLong())
    }
}