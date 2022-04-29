package com.example.dartapp.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class NumberFormatterTest {

    @Test
    fun decimalToStringManyDecimals() {
        val result = NumberFormatter.decimalToString(1.234567)
        assertThat(result).isEqualTo("1.23")
    }

    @Test
    fun decimalToStringMinimalTrailingZeros() {
        val result = NumberFormatter.decimalToString(1.1)
        assertThat(result).isEqualTo("1.1")
    }

    @Test
    fun decimalToStringZero() {
        val result = NumberFormatter.decimalToString(0)
        assertThat(result).isEqualTo("0")
    }

    @Test
    fun milliToDurationMinutes() {
        val result = NumberFormatter.milliToDurationString((2*60 + 11) * 1000)
        assertThat(result).isEqualTo("2m 11s")
    }

    @Test
    fun milliToDurationHours() {
        val result = NumberFormatter.milliToDurationString(3 * 60 * 60 * 1000 + 1000)
        assertThat(result).isEqualTo("3h 1s")
    }

    @Test
    fun milliToDurationOnlyOneUnit() {
        val result = NumberFormatter.milliToDurationString(2 * 60 * 1000, maxUnitCount = 1)
        assertThat(result).isEqualTo("2m")
    }

    @Test
    fun milliToDurationZero_nonEmptyString() {
        val result = NumberFormatter.milliToDurationString(0)
        assertThat(result).isEqualTo("0s")
    }


}