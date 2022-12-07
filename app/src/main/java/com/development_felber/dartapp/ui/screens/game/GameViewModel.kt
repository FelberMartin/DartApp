package com.development_felber.dartapp.ui.screens.game

import android.util.Log
import androidx.lifecycle.*
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.Leg
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.game.gameaction.AddDartGameAction
import com.development_felber.dartapp.game.gameaction.AddServeGameAction
import com.development_felber.dartapp.game.numberpad.NumberPadBase
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.game.numberpad.PerServeNumberPad
import com.development_felber.dartapp.ui.navigation.GameSetupHolder
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.dialog.LegFinishedDialogViewModel
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialogResult
import com.development_felber.dartapp.util.CheckoutTip
import com.development_felber.dartapp.util.GameUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PLACEHOLDER_STRING = "--"

data class PlayerUiState(
    val name: String,
    val score: PlayerScore,
    val pointsLeft: Int,
    val last: Int,
    val average: Double,
    val dartCount: Int,
)

data class GameUiState(
    val currentPlayer: PlayerRole = PlayerRole.One,
    val checkoutTip: String? = null,
    val playerUiStates: List<PlayerUiState>,
    val numberPadUiState: NumberPadUiState = NumberPadUiState(),
    val dialogUiState: DialogUiState = DialogUiState(),
)

data class NumberPadUiState(
    val numberPad: NumberPadBase = PerServeNumberPad(),
    val enterEnabled: Boolean = true,
    val disabledNumbers: List<Int> = emptyList(),
)

data class DialogUiState(
    val exitDialogOpen: Boolean = false,
    val simpleDoubleAttemptsDialogOpen: Boolean = false,
    val doubleAttemptsDialogOpen: Boolean = false,
    val checkoutDialogOpen: Boolean = false,
) {
    fun anyDialogOpen(): Boolean {
        return exitDialogOpen || simpleDoubleAttemptsDialogOpen || doubleAttemptsDialogOpen || checkoutDialogOpen
    }
}


@HiltViewModel
class GameViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    val settingsRepository: SettingsRepository,
    private val finishedLegDao: FinishedLegDao
) : ViewModel() {

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
    val dialogUiState = _dialogUiState.asStateFlow()

    private val _dartOrServeEnteredFlow = MutableSharedFlow<Int>(replay = 0)
    val dartOrServeEnteredFlow: SharedFlow<Int> = _dartOrServeEnteredFlow

    var leg = Leg()
        private set

    private var lastFinishedLeg: FinishedLeg? = null

    val gameSetup: GameSetup
        get() = GameSetupHolder.gameSetup!!

    init {
        updateUi()
    }

    fun closeClicked() {
        _dialogUiState.value = _dialogUiState.value.copy(exitDialogOpen = true)
    }

    fun onUndoClicked() {
        viewModelScope.launch {
            leg.undo()
            numberPad.value!!.clear()
            updateUi()
        }
    }

    fun onSwapNumberPadClicked() {
        if (usePerDartNumberPad) {
            _numberPad.value = PerServeNumberPad()
            leg.completeDartsToFullServe()
        } else {
            _numberPad.value = PerDartNumberPad()
        }
        updateUi()
    }

    fun onNumberTyped(number: Int) {
        if (numberPad.value!!.number.value >= 100) {
            return
        }
        viewModelScope.launch {
            numberPad.value!!.numberTyped(number)
            updateEnterButton()
            if (usePerDartNumberPad) {
                onEnterClicked()
            }
        }
    }

    private fun updateEnterButton() {
        val number = numberPad.value!!.number.value
        val valid = leg.isNumberValid(number, usePerDartNumberPad)
        _enterDisabled.value = !valid
    }

    /** Returns disabled numbers for the PerDartNumberPad. */
    fun getDisabledNumbers(modifier: PerDartNumberPad.Modifier) : List<Int> {
        val disabledNumbers = mutableListOf<Int>()
        val numbersToCheck = (1..20).toMutableList()
        numbersToCheck.add(25)
        for (i in numbersToCheck) {
            val dart = i * modifier.multiplier
            val doubleModifierEnabled =  modifier == PerDartNumberPad.Modifier.Double
            val valid = leg.isNumberValid(dart, true, doubleModifierEnabled)
            if (!valid) {
                disabledNumbers.add(i)
            }
        }
        if (modifier == PerDartNumberPad.Modifier.Triple) {
            disabledNumbers.add(25)
        }
        return disabledNumbers
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
            _dartOrServeEnteredFlow.emit(number)
            numberPad.value!!.clear()
            enterNumberToGame(number)
        }
    }

    private suspend fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            leg.applyAction(AddDartGameAction(number))
        } else {
            leg.applyAction(AddServeGameAction(number))
        }

        updateUi()
        _dialogUiState.value = _dialogUiState.value.copy(
            simpleDoubleAttemptsDialogOpen = shouldShowSimpleDoubleAttemptDialog(number),
            doubleAttemptsDialogOpen = shouldShowDoubleAttemptsDialog(number),
            checkoutDialogOpen = shouldShowCheckoutDialog()
        )

        checkLegFinished()
    }

    private suspend fun shouldShowSimpleDoubleAttemptDialog(lastDart: Int) : Boolean {
        if (!usePerDartNumberPad) {
            return false
        }
        val askForDouble = settingsRepository
            .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble).first()
        if (!askForDouble) {
            return false
        }
        if (leg.pointsLeft == 0) {
            // The game was finished with a double, so do not ask, but automatically add a double attempt.
            leg.doubleAttemptsList.add(1)
            return false
        }
        val points = leg.pointsLeft + lastDart
        val couldFinishWithDouble = points % 2 == 0 && (points == 50 || points <= 40)
        return couldFinishWithDouble
    }

    private suspend fun shouldShowDoubleAttemptsDialog(lastServe: Int) : Boolean {
        if (usePerDartNumberPad) {
            return false
        }
        val askForDouble = settingsRepository
            .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble).first()
        if (!askForDouble) {
            return false
        }
        if (leg.pointsLeft > 50 && lastServe > 0) {
            return false
        }
        val pointsBeforeServe = leg.pointsLeft + lastServe
        return CheckoutTip.checkoutTips.contains(pointsBeforeServe)
    }

    private fun updateUi() {
        _pointsLeft.value = leg.pointsLeft
        _dartCount.value = leg.dartCount
        val average = leg.getAverage(usePerDartNumberPad)
        _average.value = if (average != null) String.format("%.2f", average) else PLACEHOLDER_STRING
        val lastString = leg.getLast(usePerDartNumberPad)?.toString()
        _last.value = lastString ?: PLACEHOLDER_STRING

        _checkoutTip.value = CheckoutTip.checkoutTips[leg.pointsLeft]
        updateEnterButton()
    }

    fun dismissExitDialog() {
        _dialogUiState.value = _dialogUiState.value.copy(exitDialogOpen = false)
    }

    fun dismissLegFinishedDialog(temporary: Boolean = false) {
        _legFinished.value = false
        if (temporary) {
            viewModelScope.launch {
                delay(1000)
                _legFinished.value = true
            }
        }
    }

    fun simpleDoubleAttemptsEntered(attempt: Boolean) {
        if (attempt) {
            leg.doubleAttemptsList.add(1)
        }
        _dialogUiState.value = _dialogUiState.value.copy(simpleDoubleAttemptsDialogOpen = false)
    }

    private suspend fun shouldShowCheckoutDialog() : Boolean {
        if (usePerDartNumberPad) {
            return false
        }
        val askForCheckout = settingsRepository
            .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForCheckout).first()
        if (!askForCheckout) {
            return false
        }
        return leg.pointsLeft == 0
    }

    fun getMinimumDartCount() : Int? {
        return GameUtil.minDartCountRequiredToFinishWithinServe(leg.pointsLeftBeforeLastServe())
    }

    fun onDoubleAttemptsAndCheckoutCancelled() {
        _dialogUiState.value = _dialogUiState.value.copy(
            doubleAttemptsDialogOpen = false,
            checkoutDialogOpen = false
        )
        onUndoClicked()
    }

    fun doubleAttemptsAndCheckoutConfirmed(result: DoubleAttemptsAndCheckoutDialogResult) {
        if (result.doubleAttempts != null) {
            enterDoubleAttempts(result.doubleAttempts)
        }
        if (result.checkout != null) {
            enterCheckoutDarts(result.checkout)
        }
    }

    fun enterDoubleAttempts(attempts: Int) {
        leg.doubleAttemptsList.add(attempts)
        _dialogUiState.value = _dialogUiState.value.copy(doubleAttemptsDialogOpen = false)
        checkLegFinished()
    }

    fun enterCheckoutDarts(darts: Int) {
        leg.unusedDartCount += 3- darts
        _dialogUiState.value = _dialogUiState.value.copy(checkoutDialogOpen = false)
        updateUi()
        checkLegFinished()
    }

    private fun checkLegFinished() {
        if (leg.pointsLeft == 0 && !_dialogUiState.value.anyDialogOpen()) {
            legFinished()
        }
    }

    private fun legFinished() {
        if (legFinished.value == true) {
            return
        }
        viewModelScope.launch {
            val setDefaultDoubleAttempt = !settingsRepository
                .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble).first()
            if (setDefaultDoubleAttempt) {
                enterDoubleAttempts(1)
            }

            Log.d("GameViewModel", "Saving game to legDatabase...")
            val leg = leg.toLeg()
            finishedLegDao.insert(leg = leg)
            lastFinishedLeg = finishedLegDao.getLatestLeg()
            _legFinished.value = true   // Shows Leg Finished Dialog
        }
    }

    fun onPlayAgainClicked() {
        _legFinished.value = false
        leg = Leg()
        updateUi()
    }

    fun onModifierToggled(modifier: PerDartNumberPad.Modifier) {
        viewModelScope.launch {
            val perDartNumberPad = numberPad.value as PerDartNumberPad
            perDartNumberPad.toggleModifier(modifier)
        }
    }

    fun createLegFinishedDialogViewModel() : LegFinishedDialogViewModel {
        return LegFinishedDialogViewModel(navigationManager, lastFinishedLeg!!, finishedLegDao, settingsRepository,
            this)
    }

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }

}