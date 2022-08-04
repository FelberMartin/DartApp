package com.example.dartapp.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dartapp.game.CheckoutTip
import com.example.dartapp.game.Game
import com.example.dartapp.game.gameaction.AddDartGameAction
import com.example.dartapp.game.gameaction.AddServeGameAction
import com.example.dartapp.game.numberpad.NumberPadBase
import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.game.numberpad.PerServeNumberPad
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.navigation.command.NavigationCommand
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PLACEHOLDER_STRING = "--"

@HiltViewModel
class GameViewModel @Inject constructor(
    val navigationManager: NavigationManager,
) : NavigationViewModel(navigationManager) {

    private val _numberPad: MutableLiveData<NumberPadBase> = MutableLiveData(PerServeNumberPad())
    val numberPad: LiveData<NumberPadBase> = _numberPad

    private val _pointsLeft: MutableLiveData<Int> = MutableLiveData(501)
    val pointsLeft: LiveData<Int> = _pointsLeft

    private val _dartCount = MutableLiveData(0)
    val dartCount: LiveData<Int> = _dartCount

    private val _average = MutableLiveData<String>(PLACEHOLDER_STRING)
    val average: LiveData<String> = _average

    private val _last = MutableLiveData(PLACEHOLDER_STRING)
    val last: LiveData<String> = _last

    private val _checkoutTip: MutableLiveData<String?> = MutableLiveData(null)
    val checkoutTip: LiveData<String?> = _checkoutTip

    private val _enterDisabled = MutableLiveData(false)
    val enterDisabled: LiveData<Boolean> = _enterDisabled

    val usePerDartNumberPad
        get() = numberPad.value is PerDartNumberPad

    private val game = Game()

    init {
        updateUI()
    }

    fun closeClicked() {
        // TODO: Launch Confirm Dialog
        navigationManager.navigate(NavigationCommand.NAVIGATE_UP)
    }

    fun onUndoClicked() {
        viewModelScope.launch {
            game.undo()
            numberPad.value!!.clear()
            updateUI()
        }
    }

    fun onSwapNumberPadClicked() {
        if (usePerDartNumberPad) {
            _numberPad.value = PerServeNumberPad()
            game.completeDartsToFullServe()
        } else {
            _numberPad.value = PerDartNumberPad()
        }
        updateUI()
    }

    fun onNumberTyped(number: Int) {
        if (numberPad.value!!.number.value >= 100) {
            return
        }
        viewModelScope.launch {
            numberPad.value!!.numberTyped(number)
            updateEnterButton()
        }
    }

    private fun updateEnterButton() {
        val number = numberPad.value!!.number.value
        val valid = game.isNumberValid(number, usePerDartNumberPad)
        _enterDisabled.postValue(!valid)
    }

    fun clearNumberPad() {
        viewModelScope.launch {
            numberPad.value!!.clear()
            updateEnterButton()
        }
    }

    fun onEnterClicked() {
        viewModelScope.launch {
            val number = numberPad.value!!.number.value
            enterNumberToGame(number)
            numberPad.value!!.clear()
            updateUI()
        }
    }

    private fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            game.applyAction(AddDartGameAction(number))
        } else {
            game.applyAction(AddServeGameAction(number))
        }
    }

    private fun updateUI() {
        _pointsLeft.postValue(game.pointsLeft)
        _dartCount.postValue(game.dartCount)
        val average = game.getAverage(usePerDartNumberPad)
        _average.postValue(if (average != null) String.format("%.2f", average) else PLACEHOLDER_STRING)
        val lastString = game.getLast(usePerDartNumberPad)?.toString()
        _last.postValue(lastString ?: PLACEHOLDER_STRING)

        _checkoutTip.postValue(CheckoutTip.checkoutTips[game.pointsLeft])
        updateEnterButton()
    }

    // TODO: Dialogs
}