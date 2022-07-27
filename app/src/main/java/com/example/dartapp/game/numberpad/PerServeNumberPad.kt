package com.example.dartapp.game.numberpad

class PerServeNumberPad : NumberPadBase() {

    override suspend fun numberTyped(typed: Int) {
        if (number.value < 100) {
            _number.emit(number.value * 10 + typed)
        }
    }
}