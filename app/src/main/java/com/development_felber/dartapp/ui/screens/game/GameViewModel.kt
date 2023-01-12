package com.development_felber.dartapp.ui.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.GameState
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
import com.development_felber.dartapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val name: String,
    val playerRole: PlayerRole,
    val score: PlayerScore,
    val pointsLeft: Int,
    val last: Int?,
    val average: Double?,
    val dartCount: Int,
)

data class GameUiState(
    val currentPlayer: PlayerRole = PlayerRole.One,
    val checkoutTip: String? = null,
    val playerUiStates: List<PlayerUiState> = emptyList(),
    val numberPadUiState: NumberPadUiState = NumberPadUiState(),
    val dialogUiState: DialogUiState = DialogUiState(),
)

data class NumberPadUiState(
    val numberPad: NumberPadBase = PerServeNumberPad(),
    val enterEnabled: Boolean = true,
    val disabledNumbers: List<Int> = emptyList(),
)

data class DialogUiState(
    val finishedDialogOpen: Boolean = false,
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

    private val gameSetup: GameSetup
        get() = GameSetupHolder.gameSetup!!

    private var gameState: GameState = GameState(gameSetup)

    private val _numberPadUiState = MutableStateFlow(NumberPadUiState())
    private val numberPad: NumberPadBase
        get() = _numberPadUiState.value.numberPad
    val usePerDartNumberPad
        get() = numberPad is PerDartNumberPad

    private val _dialogUiState = MutableStateFlow(DialogUiState())

    private val _checkoutTip = MutableStateFlow<String?>(null)

    private val _dartOrServeEnteredFlow = MutableSharedFlow<Int?>(replay = 0)
    val dartOrServeEnteredFlow: SharedFlow<Int?> = _dartOrServeEnteredFlow

    private val updateRequired: MutableStateFlow<Int> = MutableStateFlow(0)

    val gameUiState = combine(_numberPadUiState, _dialogUiState, _checkoutTip, updateRequired) {
        numberPadUiState, dialogUiState, checkoutTip, _ ->
         GameUiState(
            currentPlayer = gameState.getCurrentPlayerRole(),
            checkoutTip = checkoutTip,
            playerUiStates = getPlayerUiStates(),
            numberPadUiState = numberPadUiState,
            dialogUiState = dialogUiState
        )
    }.stateIn(viewModelScope, WhileUiSubscribed, GameUiState())

    private val _legFinished = MutableLiveData(false)
    val legFinished: LiveData<Boolean> = _legFinished

    private var lastFinishedLeg: FinishedLeg? = null


    init {
        update()
    }

    private fun getPlayerUiStates() : List<PlayerUiState> {
        val playerUiStates = mutableListOf<PlayerUiState>()
        for ((role, playerGameState) in gameState.playerGameStatesByPlayerRole) {
            val leg = playerGameState.currentLeg
            playerUiStates += PlayerUiState(
                name = gameSetup.getPlayerName(role) ?: "Solo Player",
                playerRole = role,
                score = gameSetup.createPlayerScore(
                    setsWon = playerGameState.setsWonCount,
                    legsWonInCurrentSet = playerGameState.legsWonInCurrentSetCount
                ),
                pointsLeft = leg.pointsLeft,
                last = leg.getLast(perDart = usePerDartNumberPad),
                average = leg.getAverage(perDart = usePerDartNumberPad),
                dartCount = leg.dartCount,
            )
        }
        return playerUiStates
    }

    fun onCloseClicked() {
        _dialogUiState.update { it.copy(exitDialogOpen = true) }
    }

    fun onUndoClicked() {
        viewModelScope.launch {
            gameState.undo()
            numberPad.clear()
            update()
        }
    }

    fun onSwapNumberPadClicked() {
        if (usePerDartNumberPad) {
            gameState.completeDartsToFullServe()
            _numberPadUiState.update { it.copy(numberPad = PerServeNumberPad()) }
        } else {
            _numberPadUiState.update { it.copy(numberPad = PerDartNumberPad()) }
        }
        update()
    }

    fun onNumberTyped(number: Int) {
        viewModelScope.launch {
            numberPad.numberTyped(number)
            disableInvalidInputs()
            if (usePerDartNumberPad) {
                onEnterClicked()
            }
        }
    }

    private fun disableInvalidInputs() {
        if (usePerDartNumberPad) {
            _numberPadUiState.update { it.copy(
                disabledNumbers = (numberPad as PerDartNumberPad).getDisabledNumbers(gameState.currentLeg)
            ) }
        } else {
            val number = numberPad.number.value
            val valid = gameState.currentLeg.isNumberValid(number, singleDart = usePerDartNumberPad)
            _numberPadUiState.update { it.copy(enterEnabled = valid) }
        }
    }

    fun clearNumberPad() {
        viewModelScope.launch {
            numberPad.clear()
            disableInvalidInputs()
        }
    }

    fun onEnterClicked() {
        viewModelScope.launch {
            val number = numberPad.number.value
            numberPad.clear()
            enterNumberToGame(number)
            _dartOrServeEnteredFlow.emit(number)
        }
    }

    private suspend fun enterNumberToGame(number: Int) {
        if (usePerDartNumberPad) {
            gameState.applyAction(AddDartGameAction(number))
        } else {
            gameState.applyAction(AddServeGameAction(number))
        }

        update()
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
        val leg = gameState.currentLeg
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
        val leg = gameState.currentLeg
        if (leg.pointsLeft > 50 && lastServe > 0) {
            return false
        }
        val pointsBeforeServe = leg.pointsLeft + lastServe
        return CheckoutTip.checkoutTips.contains(pointsBeforeServe)
    }

    private fun update() {
        _checkoutTip.value = CheckoutTip.checkoutTips[gameState.currentLeg.pointsLeft]
        disableInvalidInputs()
        updateRequired.update { it + 1 }
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
            gameState.currentLeg.doubleAttemptsList.add(1)
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
        return gameState.currentLeg.pointsLeft == 0
    }

    fun getMinimumDartCount() : Int? {
        val pointsBefore = gameState.currentLeg.pointsLeftBeforeLastServe()
        return GameUtil.minDartCountRequiredToFinishWithinServe(pointsBefore)
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
        gameState.currentLeg.doubleAttemptsList.add(attempts)
        _dialogUiState.value = _dialogUiState.value.copy(doubleAttemptsDialogOpen = false)
        checkLegFinished()
    }

    fun enterCheckoutDarts(darts: Int) {
        gameState.currentLeg.unusedDartCount += 3- darts
        _dialogUiState.value = _dialogUiState.value.copy(checkoutDialogOpen = false)
        update()
        checkLegFinished()
    }

    private fun checkLegFinished() {
        if (gameState.currentLeg.pointsLeft == 0 && !_dialogUiState.value.anyDialogOpen()) {
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
            val leg = gameState.currentLeg.toFinishedLeg()
            finishedLegDao.insert(leg = leg)
            lastFinishedLeg = finishedLegDao.getLatestLeg()
            _legFinished.value = true   // Shows Leg Finished Dialog
        }
    }

    fun onPlayAgainClicked() {
        _legFinished.value = false
        gameState = GameState(gameSetup)
        update()
    }

    fun onModifierToggled(modifier: PerDartNumberPad.Modifier) {
        viewModelScope.launch {
            val perDartNumberPad = numberPad as PerDartNumberPad
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