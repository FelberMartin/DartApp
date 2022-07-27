package com.example.dartapp.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dartapp.game.Game
import com.example.dartapp.game.numberpad.NumberPadBase
import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.game.numberpad.PerServeNumberPad
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    navigationManager: NavigationManager,
) : NavigationViewModel(navigationManager) {

    private val _numberPad: MutableLiveData<NumberPadBase> = MutableLiveData(PerServeNumberPad())
    val numberPad: LiveData<NumberPadBase> = _numberPad

    val usePerServeNumberPad
        get() = numberPad.value is PerServeNumberPad

    private val game: Game? = null

    fun closeClicked() {
        // Launch Confirm Dialog
    }

    suspend fun onUndoClicked() {
        // TOOD: game.undo()
        numberPad.value!!.clear()
    }

    fun onSwitchNumPadClicked() {
        if (usePerServeNumberPad) {
            _numberPad.postValue(PerDartNumberPad())
        } else {
            _numberPad.postValue(PerServeNumberPad())
        }
    }

    fun onEnterClicked() {
        val number = numberPad.value!!.number

    }


}