package com.development_felber.dartapp.util.categorized_sort

import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.ui.screens.history.CategorizedSortTypeBase
import java.time.LocalDateTime
import java.time.ZoneOffset

object DateCategorizedSortType : CategorizedSortTypeBase(name = "Date", byDefaultDescending = true) {
    override val categories: List<Category>
        get() = listOf(
            Category("Today", LocalDateTime.now().withHour(0).toEpochSecond(ZoneOffset.UTC)),
            Category("Last days", LocalDateTime.now().minusDays(7).toEpochSecond(ZoneOffset.UTC)),
            Category("Last weeks", LocalDateTime.now().minusWeeks(5).toEpochSecond(ZoneOffset.UTC)),
            Category("Last months", LocalDateTime.now().minusMonths(12).toEpochSecond(ZoneOffset.UTC)),
            Category("A long time ago...", 0),
        )

    override fun valueForLeg(leg: FinishedLeg): Number {
        val endDateTime = Converters.toLocalDateTime(leg.endTime)
        return endDateTime.toEpochSecond(ZoneOffset.UTC)
    }
}