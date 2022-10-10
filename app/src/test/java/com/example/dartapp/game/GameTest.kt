package com.example.dartapp.game

import com.example.dartapp.game.gameaction.AddServeGameAction
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GameTest {

    @Test
    fun getAverage() {
        val game = Game()
        game.applyAction(AddServeGameAction(180))
        game.applyAction(AddServeGameAction(180))
        game.applyAction(AddServeGameAction(139))
        game.applyAction(AddServeGameAction(2))
        game.unusedDartCount = 2

        val dartCount = 10.0

        val expected = 501.0 / (dartCount / 3.0)
        assertThat(game.getAverage()).isEqualTo(expected)
    }

    @Test
    fun `try to finish outside of finish range (without double), invalid serve`() {
        val game = Game()
        game.applyAction(AddServeGameAction(180))
        game.applyAction(AddServeGameAction(141))
        val valid = game.isNumberValid(180, singleDart = false)
        assertThat(valid).isFalse()
    }

}