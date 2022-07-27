package com.example.dartapp.game.numberpad

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class NumberPadBase {

    protected val _number = MutableStateFlow(0)
    val number: StateFlow<Int> = _number

    abstract suspend fun numberTyped(typed: Int)

    open suspend fun clear() {
        _number.emit(0)
    }
}