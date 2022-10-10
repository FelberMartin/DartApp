package com.example.dartapp.ui.screens.game.testutil

import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.getOrAwaitValueTest
import com.example.dartapp.ui.screens.game.GameViewModel

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