package com.development_felber.dartapp.game.numberpad

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class NumberPadBase {

    protected val _number = MutableStateFlow(0)
    val number = _number.asStateFlow()

    abstract suspend fun numberTyped(typed: Int)

    open suspend fun clear() {
        _number.emit(0)
    }
}