package com.example.dartapp.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.game.Game
import com.example.dartapp.game.gameaction.AddDartGameAction
import com.example.dartapp.game.gameaction.AddServeGameAction
import com.example.dartapp.game.numberpad.NumberPadBase
import com.example.dartapp.game.numberpad.PerDartNumberPad
import com.example.dartapp.game.numberpad.PerServeNumberPad
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.game.dialog.DialogUiState
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.util.CheckoutTip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PLACEHOLDER_STRING = "--"

@HiltViewModel
class GameViewModel @Inject constructor(
    val navigationManager: NavigationManager,
    val settingsRepository: SettingsRepository
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

    private val _legFinished = MutableLiveData(false)
    val legFinished: LiveData<Boolean> = _legFinished

    val usePerDartNumberPad
        get() = numberPad.value is PerDartNumberPad

    private val _dialogUiState = MutableLiveData(DialogUiState())
    val dialogUiState: LiveData<DialogUiState> = _dialogUiState

    private var game = Game()

    init {
        updateUI()
    }

    fun closeClicked() {
        dialogUiState.value!!.exitDialogOpen = true
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

    // TODO: Weird behaviour when entering numbers

    fun onEnterClicked() {
        viewModelScope.launch {
            val number = numberPad.value!!.number.value
            enterNumberToGame(number)
            numberPad.value!!.clear()
            updateUI()
            checkLegFinished()
        }
    }

    private suspend fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            game.applyAction(AddDartGameAction(number))
            if (shouldShowSimpleDoubleAttemptDialog()) {
                dialogUiState.value!!.simpleDoubleAttemptsDialogOpen = true
            }
        } else {
            game.applyAction(AddServeGameAction(number))
            if (shouldShowDoubleAttemptsDialog()) {
                dialogUiState.value!!.doubleAttemptsDialogOpen = true
            }
        }

    }

    private suspend fun shouldShowSimpleDoubleAttemptDialog() : Boolean {
        if (!usePerDartNumberPad) {
            return false
        }
        if (!settingsRepository.askForDoubleFlow.last()) {
            return false
        }
        val points = game.pointsLeft
        return points % 2 == 0 && (points == 50 || points <= 40)
    }

    private suspend fun shouldShowDoubleAttemptsDialog() : Boolean {
        if (usePerDartNumberPad) {
            return false
        }
        if (settingsRepository.askForDoubleFlow.last()) {
            return false
        }
        return CheckoutTip.checkoutTips.contains(pointsLeft.value!!)
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

    fun dismissExitDialog() {
        dialogUiState.value!!.exitDialogOpen = false
    }

    fun simpleDoubleAttemptsEntered(attempt: Boolean) {
        if (attempt) {
            game.doubleAttemptsList.add(1)
        }
        dialogUiState.value!!.simpleDoubleAttemptsDialogOpen = false
    }

    fun doubleAttemptsEntered(attempts: Int) {
        game.doubleAttemptsList.add(attempts)

        viewModelScope.launch {
            if (shouldShowCheckoutDialog()) {
                dialogUiState.value!!.checkoutDialogOpen = true
            } else {
                dialogUiState.value!!.doubleAttemptsDialogOpen = false
            }
        }
    }

    private suspend fun shouldShowCheckoutDialog() : Boolean {
        if (usePerDartNumberPad) {
            return false
        }
        if (pointsLeft.value!! > 0) {
            return false
        }
        if (!settingsRepository.askForCheckoutFlow.last()) {
            return false
        }
        if (getLastDoubleAttempts() == 3) {
            return false
        }
        return true
    }

    fun getLastDoubleAttempts() : Int {
        return game.doubleAttemptsList.last()
    }

    fun checkoutDartsEntered(darts: Int) {
        game.unusedDartCount += 3 - darts
        dialogUiState.value!!.checkoutDialogOpen = false
    }

    private fun checkLegFinished() {
        if (game.pointsLeft == 0) {
            waitForDialogsToClose { legFinished() }
        }
    }

    private fun waitForDialogsToClose(action: () -> Unit) {
        viewModelScope.launch {
            dialogUiState.asFlow().collect {
                if (!dialogUiState.value!!.anyDialogOpen()) {
                    action.invoke()
                }
            }
        }
    }

    private fun legFinished() {
        _legFinished.value = true   // Shows Leg Finished Dialog

        // TODO: enter leg into database
    }

    fun onPlayAgainClicked() {
        _legFinished.value = false
        game = Game()
        updateUI()
    }

}