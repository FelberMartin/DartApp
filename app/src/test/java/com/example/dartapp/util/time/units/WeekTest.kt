package com.example.dartapp.util.time.units

import com.google.common.truth.Truth
import org.junit.Test
import java.util.*

class WeekTest {

    @Test
    fun millisAfterGoingBack() {
        val saturday = Date.UTC(122, 5, 4, 5, 5, 5)
        val result = Week.millisAfterGoingBack(1, saturday)
        val twoWeeksBefore = Date.UTC(122, 4, 23, 0, 0, 0)
        Truth.assertThat(result).isEqualTo(twoWeeksBefore)
    }
}