package com.development_felber.dartapp.ui.screens.game.testutil

import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.screens.game.GameViewModel

class PerDartNumPadEnter(
    private val baseNumber: Int,
    private val modifier: PerDartNumberPad.Modifier = PerDartNumberPad.Modifier.None
) {

    fun apply(viewModel: GameViewModel) {
        val numPad = viewModel.numberPad.getOrAwaitValueTest() as PerDartNumberPad
        if (numPad.modifier.value != modifier) {
            viewModel.onModifierToggled(modifier)
        }
        viewModel.onNumberTyped(baseNumber)
    }
}