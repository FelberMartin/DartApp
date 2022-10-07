package com.example.dartapp.util.categorized_sort

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.ui.screens.history.CategorizedSortTypeBase
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

    override fun valueForLeg(leg: Leg): Number {
        val endDateTime = Converters.toLocalDateTime(leg.endTime)
        return endDateTime.toEpochSecond(ZoneOffset.UTC)
    }
}