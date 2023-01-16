package com.development_felber.dartapp.ui.screens.game.testutil

import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.screens.game.GameViewModel
import kotlinx.coroutines.flow.first

class PerDartNumPadEnter(
    private val baseNumber: Int,
    private val modifier: PerDartNumberPad.Modifier = PerDartNumberPad.Modifier.None
) {

    suspend fun apply(viewModel: GameViewModel) {
        val numPad = viewModel.gameUiState.first().numberPadUiState.numberPad as PerDartNumberPad
        if (numPad.modifier.value != modifier) {
            viewModel.onModifierToggled(modifier)
        }
        viewModel.onNumberTyped(baseNumber)
    }
}