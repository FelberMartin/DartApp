package com.example.dartapp.ui.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.persistent.database.LegDatabaseDao
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PLACEHOLDER_STRING = "--"

@HiltViewModel
class GameViewModel @Inject constructor(
    val navigationManager: NavigationManager,
    val settingsRepository: SettingsRepository,
    private val legDatabaseDao: LegDatabaseDao
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

    private val _dialogUiState = MutableStateFlow(DialogUiState())
    val dialogUiState: LiveData<DialogUiState> = _dialogUiState.asLiveData()

    var game = Game()
        private set

    init {
        update()
    }

    fun closeClicked() {
        // TODO: 21.09.22 Also do this onBackPressed
        _dialogUiState.update { currentState ->
            currentState.copy(exitDialogOpen = true)
        }
    }

    fun onUndoClicked() {
        viewModelScope.launch {
            game.undo()
            numberPad.value!!.clear()
            update()
        }
    }

    fun onSwapNumberPadClicked() {
        if (usePerDartNumberPad) {
            _numberPad.value = PerServeNumberPad()
            game.completeDartsToFullServe()
        } else {
            _numberPad.value = PerDartNumberPad()
        }
        update()
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
        _enterDisabled.value = !valid
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
            update()
        }
    }

    private suspend fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            game.applyAction(AddDartGameAction(number))
        } else {
            game.applyAction(AddServeGameAction(number))
        }

        update()
        _dialogUiState.update { state ->
            state.copy(
                simpleDoubleAttemptsDialogOpen = shouldShowSimpleDoubleAttemptDialog(number),
                doubleAttemptsDialogOpen = shouldShowDoubleAttemptsDialog(),
                checkoutDialogOpen = shouldShowCheckoutDialog()
            )
        }
    }

    private suspend fun shouldShowSimpleDoubleAttemptDialog(lastDart: Int) : Boolean {
        if (!usePerDartNumberPad) {
            return false
        }
        if (!settingsRepository.askForDoubleFlow.first()) {
            return false
        }
        val points = game.pointsLeft + lastDart
        return points % 2 == 0 && (points == 50 || points <= 40)
    }

    private suspend fun shouldShowDoubleAttemptsDialog() : Boolean {
        if (usePerDartNumberPad) {
            return false
        }
        if (!settingsRepository.askForDoubleFlow.first()) {
            return false
        }
        return CheckoutTip.checkoutTips.contains(pointsLeft.value!!)
    }

    private fun update() {
        _pointsLeft.value = game.pointsLeft
        _dartCount.value = game.dartCount
        val average = game.getAverage(usePerDartNumberPad)
        _average.value = if (average != null) String.format("%.2f", average) else PLACEHOLDER_STRING
        val lastString = game.getLast(usePerDartNumberPad)?.toString()
        _last.value = lastString ?: PLACEHOLDER_STRING

        _checkoutTip.value = CheckoutTip.checkoutTips[game.pointsLeft]
        updateEnterButton()
        checkLegFinished()
    }

    fun dismissExitDialog() {
        _dialogUiState.update { state -> state.copy(exitDialogOpen = false) }
    }

    fun simpleDoubleAttemptsEntered(attempt: Boolean) {
        if (attempt) {
            game.doubleAttemptsList.add(1)
        }
        _dialogUiState.update { state -> state.copy(simpleDoubleAttemptsDialogOpen = false) }
    }

    fun doubleAttemptsEntered(attempts: Int) {
        game.doubleAttemptsList.add(attempts)

        viewModelScope.launch {
            _dialogUiState.update { state ->
                state.copy(doubleAttemptsDialogOpen = false, checkoutDialogOpen = shouldShowCheckoutDialog())
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
        if (!settingsRepository.askForCheckoutFlow.first()) {
            return false
        }
        if (getLastDoubleAttempts() == 3) {
            return false
        }
        return true
    }

    fun getLastDoubleAttempts() : Int {
        return game.doubleAttemptsList.lastOrNull() ?: 0
    }

    fun checkoutDartsEntered(darts: Int) {
        game.unusedDartCount += 3 - darts
        _dialogUiState.update { state -> state.copy(checkoutDialogOpen = false) }
        update()
    }

    private fun checkLegFinished() {
        if (game.pointsLeft == 0) {
            legFinished()
        }
    }

    private fun legFinished() {
        if (legFinished.value == true) {
            return
        }
        _legFinished.value = true   // Shows Leg Finished Dialog
        viewModelScope.launch {
            Log.d("GameViewModel", "Saving game to legDatabase...")
            legDatabaseDao.insert(leg = game.toLeg())
        }
    }

    fun onPlayAgainClicked() {
        _legFinished.value = false
        game = Game()
        update()
    }

    fun onDoubleModifierToggled() {
        viewModelScope.launch {
            val perDartNumberPad = numberPad.value as PerDartNumberPad
            perDartNumberPad.toggleDoubleModifier()
        }
    }

    fun onTripleModifierToggled() {
        viewModelScope.launch {
            val perDartNumberPad = numberPad.value as PerDartNumberPad
            perDartNumberPad.toggleTripleModifier()
        }
    }

}