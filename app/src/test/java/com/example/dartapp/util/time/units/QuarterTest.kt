package com.example.dartapp.util.time.units

import com.google.common.truth.Truth
import org.junit.Test
import java.util.*

class QuarterTest {

    @Test
    fun millisAfterGoingBack() {
        val q2 = Date.UTC(122, 5, 4, 5, 5, 5)
        val result = Quarter.millisAfterGoingBack(1, q2)

        /** See MonthTest.java */
        val bugHourOffset = 1
        val q1 = Date.UTC(122, 0, 0, bugHourOffset, 0, 0)

        println("Result: ${Date(result)}")
        println("Expected: ${Date(q1)}")
        Truth.assertThat(result).isEqualTo(q1)
    }
}