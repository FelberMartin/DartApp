package com.example.dartapp.graphs.versus

import com.example.dartapp.database.Leg
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WeekdayVersusTypeTest {

    private val weekdayVersusType = WeekdayVersusType()

    @Test
    fun partitionLegs_listForEveryWeekDay() {
        val legs = listOf(Leg())
        val result = weekdayVersusType.partitionLegs(legs)
        assertThat(result.keys.size).isEqualTo(7)
    }
}