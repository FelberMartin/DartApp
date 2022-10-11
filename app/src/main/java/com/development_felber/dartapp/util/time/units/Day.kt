package com.development_felber.dartapp.util.time.units

import android.icu.text.DateFormat
import com.development_felber.dartapp.util.time.TimeUnit
import java.time.LocalDateTime

object Day : TimeUnit(
    uniquePattern = DateFormat.YEAR_NUM_MONTH_DAY,
    uiPattern = DateFormat.ABBR_WEEKDAY
) {

    override fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime): LocalDateTime {
        return dateTime.minusDays(unitCount.toLong())
    }
}