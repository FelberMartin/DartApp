package com.development_felber.dartapp.game

import com.development_felber.dartapp.game.gameaction.AddServeGameAction
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class LegTest {

    private lateinit var gameState: GameState

    @Before
    fun setUp() {
        gameState = GameState(GameSetup.Solo)
    }

    @Test
    fun getAverage() {
        gameState.applyAction(AddServeGameAction(180))
        gameState.applyAction(AddServeGameAction(180))
        gameState.applyAction(AddServeGameAction(139))
        gameState.applyAction(AddServeGameAction(2))
        gameState.currentLeg.unusedDartCount = 2

        val dartCount = 10.0
        val expected = 501.0 / (dartCount / 3.0)
        assertThat(gameState.currentLeg.getAverage()).isEqualTo(expected)
    }

    @Test
    fun `try to finish outside of finish range (without double), invalid serve`() {
        gameState.applyAction(AddServeGameAction(180))
        gameState.applyAction(AddServeGameAction(141))
        val valid = gameState.currentLeg.isNumberValid(180, singleDart = false)
        assertThat(valid).isFalse()
    }

}