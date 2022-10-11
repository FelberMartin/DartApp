package com.development_felber.dartapp.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GameUtilTest {

    @Test
    fun `two categories`() {
        val inputServes = listOf(56, 69, 180, 39, 2, 121, 111, 34)
        val result = GameUtil.partitionSizeForLowerLimits(
            inputServes,
            listOf(0, 100)
        )

        assertThat(result).isEqualTo(
            mapOf(
                0 to 5,
                100 to 3
            )
        )
    }

    @Test
    fun `default categories`() {
        val inputServes = listOf(56, 69, 180, 39, 2, 121, 111, 34)
        val result = GameUtil.partitionSizeForLowerLimits(
            inputServes,
            listOf(0, 60, 100, 140)
        )

        assertThat(result).isEqualTo(
            mapOf(
                0 to 4,
                60 to 1,
                100 to 2,
                140 to 1
            )
        )
    }

}