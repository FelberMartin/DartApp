package com.development_felber.dartapp.game.numberpad

import kotlinx.coroutines.flow.update

class PerServeNumberPad : NumberPadBase() {

    override suspend fun numberTyped(typed: Int) {
        if (number.value < 100) {
            _number.update { n -> n * 10 + typed }
        }
    }
}