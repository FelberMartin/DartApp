package com.development_felber.dartapp.game.numberpad

import com.development_felber.dartapp.game.Leg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerDartNumberPad : NumberPadBase() {

    enum class Modifier(val multiplier: Int) {
        None(1),
        Double(2),
        Triple(3)
    }

    private var baseNumber: Int = 0

    private val _modifier = MutableStateFlow(Modifier.None)
    val modifier = _modifier.asStateFlow()

    override suspend fun numberTyped(typed: Int) {
        baseNumber = typed
        recomputeNumber()
    }

    private suspend fun recomputeNumber() {
        _number.emit(modifier.value.multiplier * baseNumber)
    }

    override suspend fun clear() {
        super.clear()
        _modifier.value = Modifier.None
        baseNumber = 0
    }

    suspend fun toggleModifier(toggledModifier: Modifier) {
        if (toggledModifier == modifier.value) {
            _modifier.value = Modifier.None
        } else {
            _modifier.value = toggledModifier
        }
        recomputeNumber()
    }

    /** Returns disabled numbers for the PerDartNumberPad. */
    fun getDisabledNumbers(leg: Leg) : List<Int> {
        val disabledNumbers = mutableListOf<Int>()
        val numbersToCheck = (1..20).toMutableList()
        numbersToCheck.add(25)
        for (i in numbersToCheck) {
            val dart = i * modifier.value.multiplier
            val doubleModifierEnabled =  modifier.value == Modifier.Double
            val valid = leg.isNumberValid(dart, true, doubleModifierEnabled)
            if (!valid) {
                disabledNumbers.add(i)
            }
        }
        if (modifier.value == Modifier.Triple) {
            disabledNumbers.add(25)
        }
        return disabledNumbers
    }

}