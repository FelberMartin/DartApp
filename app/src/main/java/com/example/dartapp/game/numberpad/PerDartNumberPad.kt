package com.example.dartapp.game.numberpad

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerDartNumberPad : NumberPadBase() {

    private var baseNumber: Int = 0

    private val _doubleModifierEnabled = MutableStateFlow(false)
    val doubleModifierEnabled: StateFlow<Boolean> = _doubleModifierEnabled

    private val _tripleModifierEnabled = MutableStateFlow(false)
    val tripleModifierEnabled: StateFlow<Boolean> = _tripleModifierEnabled

    override suspend fun numberTyped(typed: Int) {
        baseNumber = typed
        recomputeNumber()
    }

    private suspend fun recomputeNumber() {
        var modifier = 1
        if (doubleModifierEnabled.value) {
            modifier = 2
        }
        if (tripleModifierEnabled.value) {
            modifier = 3
        }
        _number.emit(modifier * baseNumber)
    }

    override suspend fun clear() {
        super.clear()
        _doubleModifierEnabled.emit(false)
        _tripleModifierEnabled.emit(false)
    }
}