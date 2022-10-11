package com.development_felber.dartapp.util.time.units

import android.icu.text.DateFormat
import com.development_felber.dartapp.util.time.TimeUnit
import java.time.LocalDateTime

object Quarter : TimeUnit(
    uniquePattern = DateFormat.YEAR_QUARTER,
    uiPattern = DateFormat.YEAR_ABBR_QUARTER
) {

    override fun afterGoingBack(unitCount: Int, dateTime: LocalDateTime): LocalDateTime {
        return dateTime.minusMonths(unitCount * 3L)
    }
}