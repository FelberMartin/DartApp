package com.example.dartapp.util.time.units

import com.google.common.truth.Truth
import org.junit.Test
import java.util.*

class DayTest {

    @Test
    fun millisAfterGoingBack() {
        val secondDay = Date.UTC(100, 0, 2, 5, 5, 5)
        val result = Day.millisAfterGoingBack(1, secondDay)
        val expected = Date.UTC(100, 0, 1, 0, 0, 0)
        Truth.assertThat(result).isEqualTo(expected)
    }
}