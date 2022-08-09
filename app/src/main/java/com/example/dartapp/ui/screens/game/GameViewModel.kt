package com.example.dartapp.ui.screens.game

import androidx.lifecycle.*
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

    private val _dialogUiState = MutableStateFlow(DialogUiState())
    val dialogUiState: LiveData<DialogUiState> = _dialogUiState.asLiveData()

    var game = Game()
        private set

    init {
        updateUI()
        checkLegFinished { legFinished() }
    }

    fun closeClicked() {
        _dialogUiState.update { currentState ->
            currentState.copy(exitDialogOpen = true)
        }
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
        }
    }

    private suspend fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            game.applyAction(AddDartGameAction(number))
        } else {
            game.applyAction(AddServeGameAction(number))
        }

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
        return game.doubleAttemptsList.last()
    }

    fun checkoutDartsEntered(darts: Int) {
        game.unusedDartCount += 3 - darts
        _dialogUiState.update { state -> state.copy(checkoutDialogOpen = false) }

    }

    private fun checkLegFinished(action: () -> Unit) {
        viewModelScope.launch {
            dialogUiState.asFlow().collect {
                if (!_dialogUiState.value.anyDialogOpen() && game.pointsLeft == 0) {
                    println("flow collect Action invoked")
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