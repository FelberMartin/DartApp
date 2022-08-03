package com.example.dartapp.game.numberpad

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerDartNumberPad : NumberPadBase() {

    private var baseNumber: Int = 0

    private val _doubleModifierEnabled = MutableStateFlow(false)
    val doubleModifierEnabled = _doubleModifierEnabled.asStateFlow()

    private val _tripleModifierEnabled = MutableStateFlow(false)
    val tripleModifierEnabled = _tripleModifierEnabled.asStateFlow()

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

    suspend fun toggleDoubleModifier() {
        val currentlyEnabled = doubleModifierEnabled.value
        if (!currentlyEnabled && tripleModifierEnabled.value) {
            _tripleModifierEnabled.emit(false)
        }
        _doubleModifierEnabled.emit(!currentlyEnabled)
        recomputeNumber()
    }

    suspend fun toggleTripleModifier() {
        val currentlyEnabled = tripleModifierEnabled.value
        if (!currentlyEnabled && doubleModifierEnabled.value) {
            _doubleModifierEnabled.emit(false)
        }
        _tripleModifierEnabled.emit(!currentlyEnabled)
        recomputeNumber()
    }
}