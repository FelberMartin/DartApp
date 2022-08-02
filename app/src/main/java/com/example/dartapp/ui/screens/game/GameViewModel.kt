package com.example.dartapp.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class GameViewModel @Inject constructor(
    val navigationManager: NavigationManager,
) : NavigationViewModel(navigationManager) {

    private val _numberPad: MutableLiveData<NumberPadBase> = MutableLiveData(PerServeNumberPad())
    val numberPad: LiveData<NumberPadBase> = _numberPad

    val usePerServeNumberPad
        get() = numberPad.value is PerServeNumberPad

    private val game = Game()

    fun closeClicked() {
        // TODO: Launch Confirm Dialog
        navigationManager.navigate(NavigationCommand.NAVIGATE_UP)
    }

    fun onUndoClicked() {
        viewModelScope.launch {
            game.undo()
            numberPad.value!!.clear()
        }
    }

    fun onSwapNumberPadClicked() {
        if (usePerServeNumberPad) {
            _numberPad.postValue(PerDartNumberPad())
        } else {
            _numberPad.postValue(PerServeNumberPad())
        }
    }

    fun onNumberTyped(number: Int) {
        viewModelScope.launch {
            numberPad.value!!.numberTyped(number)
        }
    }

    fun clearNumberPad() {
        viewModelScope.launch {
            numberPad.value!!.clear()
        }
    }

    fun onEnterClicked() {
        viewModelScope.launch {
            val number = numberPad.value!!.number.value

            if (usePerServeNumberPad) {
                game.applyAction(AddServeGameAction(number))
            } else {
                game.applyAction(AddDartGameAction(number))
            }

            numberPad.value!!.clear()
        }
    }

    // TODO: values from the game (avg, last, pointsLeft) to show in the UI
}