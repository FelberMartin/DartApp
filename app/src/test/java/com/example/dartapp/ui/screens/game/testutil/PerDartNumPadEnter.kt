package com.example.dartapp.ui.screens.game.testutil

import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.getOrAwaitValueTest
import com.example.dartapp.ui.screens.game.GameViewModel

class PerDartNumPadEnter(
    private val baseNumber: Int,
    private val double: Boolean = false,
    private val triple: Boolean = false,
) {

    fun apply(viewModel: GameViewModel) {
        val numPad = viewModel.numberPad.getOrAwaitValueTest() as PerDartNumberPad
        if (numPad.doubleModifierEnabled.value != double) {
            viewModel.onDoubleModifierToggled()
        }
        if (numPad.tripleModifierEnabled.value != triple) {
            viewModel.onTripleModifierToggled()
        }
        viewModel.onNumberTyped(baseNumber)
        viewModel.onEnterClicked()
    }
}