package com.example.dartapp.ui.screens.history

import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.Leg
import java.time.ZoneOffset

abstract class SortType(
    val name: String,
    val byDefaultDescending: Boolean
) {

    fun sortLegs(legs: List<Leg>, descending: Boolean) : List<Leg> {
        val sorted = legs.sortedBy(this::valueForLeg)
        if (descending) {
            return sorted.reversed()
        }
        return sorted
    }

    abstract fun valueForLeg(leg: Leg) : Double

    object DateSortType : SortType(name = "Date", byDefaultDescending = true) {
        override fun valueForLeg(leg: Leg): Double {
            val endDateTime = Converters.toLocalDateTime(leg.endTime)
            return endDateTime.toEpochSecond(ZoneOffset.UTC).toDouble()
        }
    }

    object DartCountSortType : SortType(name = "Dart Count", byDefaultDescending = false) {
        override fun valueForLeg(leg: Leg): Double {
            return leg.dartCount.toDouble()
        }
    }

    object CheckoutSortType : SortType(name = "Checkout", byDefaultDescending = true) {
        override fun valueForLeg(leg: Leg): Double {
            return leg.checkout.toDouble()
        }
    }

    object PlaceHolderSortType: SortType("", false) {
        override fun valueForLeg(leg: Leg): Double {
            return 0.0
        }
    }

    companion object {
        val all = listOf(DateSortType, DartCountSortType, CheckoutSortType)
    }
}