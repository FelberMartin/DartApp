package com.development_felber.dartapp.util.time.units

import com.development_felber.dartapp.util.time.TimeUnit
import java.time.LocalDateTime

object Day : TimeUnit(
    uniquePattern = "yMd" /* DateFormat.YEAR_NUM_MONTH_DAY */,
    uiPattern = "E" /* DateFormat.ABBR_WEEKDAY */,
    // Removed the constants form the android.icu.text.DateFormat class because they are
    // not available in the Android API 21.
) {

    override fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime): LocalDateTime {
        return dateTime.minusDays(unitCount.toLong())
    }
}