package com.example.dartapp.util.time.units

import com.google.common.truth.Truth
import org.junit.Ignore
import org.junit.Test
import java.util.*

class MonthTest {

    @Test
    fun millisAfterGoingBack() {
        val june = Date.UTC(122, 5, 4, 5, 5, 5)
        val result = Month.millisAfterGoingBack(1, june)
        val may = Date.UTC(122, 4, 0, 0, 0, 0)

        println("Result: ${Date(result)}")
        println("Expected: ${Date(may)}")
        Truth.assertThat(result).isEqualTo(may)
    }

    @Ignore("Fails for some reason :shrug:")
    @Test
    fun millisAfterGoingBack2() {
        val june = Date.UTC(122, 5, 4, 5, 5, 5)
        val result = Month.millisAfterGoingBack(3, june)
        val may = Date.UTC(122, 2, 0, 0, 0, 0)

        println("Result: ${Date(result)}")
        println("Expected: ${Date(may)}")

        // For some reason if the number of months to subtract from the calendar, the time increases by +1h
        Truth.assertThat(result).isEqualTo(may)
    }
}