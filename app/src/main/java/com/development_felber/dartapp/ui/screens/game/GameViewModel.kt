package com.development_felber.dartapp.ui.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.GameState
import com.development_felber.dartapp.game.GameStatus
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.game.gameaction.AddDartGameAction
import com.development_felber.dartapp.game.gameaction.AddServeGameAction
import com.development_felber.dartapp.game.numberpad.NumberPadBase
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.game.numberpad.PerServeNumberPad
import com.development_felber.dartapp.ui.navigation.GameSetupHolder
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.dialog.GameDialogManager
import com.development_felber.dartapp.ui.screens.game.dialog.SoloGameFinishedDialogViewModel
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialogResult
import com.development_felber.dartapp.util.CheckoutTip
import com.development_felber.dartapp.util.GameUtil
import com.development_felber.dartapp.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    val dialogToShow: GameDialogManager.DialogType? = null,
    val playerUiStates: List<PlayerUiState> = emptyList(),
    val numberPadUiState: NumberPadUiState = NumberPadUiState(),
)

data class NumberPadUiState(
    val numberPad: NumberPadBase = PerServeNumberPad(),
    val enterEnabled: Boolean = true,
    val disabledNumbers: List<Int> = emptyList(),
)


@HiltViewModel
class GameViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    val settingsRepository: SettingsRepository,
    private val finishedLegDao: FinishedLegDao,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val gameSetup: GameSetup
        get() = GameSetupHolder.gameSetup!!

    var gameState: GameState = GameState(gameSetup)

    private val _numberPadUiState = MutableStateFlow(NumberPadUiState())
    private val numberPad: NumberPadBase
        get() = _numberPadUiState.value.numberPad
    val usePerDartNumberPad
        get() = numberPad is PerDartNumberPad

    private val _checkoutTip = MutableStateFlow<String?>(null)

    private val _dartOrServeEnteredFlow = MutableSharedFlow<Int?>(replay = 0)
    val dartOrServeEnteredFlow: SharedFlow<Int?> = _dartOrServeEnteredFlow

    private val updateRequired: MutableStateFlow<Int> = MutableStateFlow(0)

    private val dialogManager = GameDialogManager()

    val gameUiState = combine(_numberPadUiState, dialogManager.currentDialog, _checkoutTip, updateRequired) {
        numberPadUiState, dialog, checkoutTip, _ ->
         GameUiState(
            currentPlayer = gameState.getCurrentPlayerRole(),
            checkoutTip = checkoutTip,
            dialogToShow = dialog,
            playerUiStates = getPlayerUiStates(),
            numberPadUiState = numberPadUiState,
        )
    }.stateIn(viewModelScope, WhileUiSubscribed, GameUiState())

    private var gameSaved = false

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
        dialogManager.openDialog(GameDialogManager.DialogType.ExitGame)
    }

    fun onUndoClicked() {
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
            numberPad.clear()
            disableInvalidInputs()
        }
    }

    fun onEnterClicked() {
        viewModelScope.launch(dispatcher) {
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
        updateDialogs(number)
        checkLegFinished()
    }

    private suspend fun updateDialogs(lastEnteredNumber: Int) {
        dialogManager.determineDialogsToOpen(
            lastNumberEntered = lastEnteredNumber,
            gameState = gameState,
            settingsRepository = settingsRepository,
            usePerDartNumberPad = usePerDartNumberPad,
        )
    }


    private fun update() {
        _checkoutTip.value = CheckoutTip.checkoutTips[gameState.currentLeg.pointsLeft]
        disableInvalidInputs()
        updateRequired.update { it + 1 }
    }

    fun dismissExitDialog() {
        dialogManager.closeDialog()
    }

    fun dismissLegFinishedDialog(temporary: Boolean = false) {
        dialogManager.closeDialog()
        if (temporary) {
            viewModelScope.launch(dispatcher) {
                delay(1000)
                dialogManager.openDialog(GameDialogManager.DialogType.GameFinished)
            }
        }
    }

    fun simpleDoubleAttemptsEntered(attempt: Boolean) {
        if (attempt) {
            gameState.currentLeg.doubleAttemptsList.add(1)
        }
        dialogManager.closeDialog()
    }

    fun getMinimumDartCount() : Int? {
        val pointsBefore = gameState.currentLeg.pointsLeftBeforeLastServe()
        return GameUtil.minDartCountRequiredToFinishWithinServe(pointsBefore)
    }

    fun onDoubleAttemptsAndCheckoutCancelled() {
        dialogManager.closeDialog()
        onUndoClicked()
    }

    fun doubleAttemptsAndCheckoutConfirmed(result: DoubleAttemptsAndCheckoutDialogResult) {
        if (result.doubleAttempts != null) {
            enterDoubleAttempts(result.doubleAttempts)
        }
        if (result.checkout != null) {
            enterCheckoutDarts(result.checkout)
        }
        dialogManager.closeDialog()
        update()
        checkLegFinished()
    }

    fun enterDoubleAttempts(attempts: Int) {
        gameState.currentLeg.doubleAttemptsList.add(attempts)
    }

    fun enterCheckoutDarts(darts: Int) {
        gameState.currentLeg.unusedDartCount += 3- darts
    }

    private fun checkLegFinished() {
        val anyEnterDataToGameDialogOpen = when (dialogManager.currentDialog.value) {
            is GameDialogManager.DialogType.AskForDoubleSimple,
            is GameDialogManager.DialogType.AskForDoubleAndOrCheckout -> true
            else -> false
        }
        if (gameState.gameStatus == GameStatus.Finished && !anyEnterDataToGameDialogOpen) {
            legFinished()
        }
    }

    private fun legFinished() {
        viewModelScope.launch(dispatcher) {
            if (gameSaved) {
                return@launch
            }
            gameSaved = true

            val setDefaultDoubleAttempt = !settingsRepository
                .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble).first()
            if (setDefaultDoubleAttempt) {
                enterDoubleAttempts(1)
            }

            Log.d("GameViewModel", "Saving game to legDatabase...")
            val leg = gameState.currentLeg.toFinishedLeg()
            finishedLegDao.insert(leg = leg)
        }
    }

    fun onPlayAgainClicked() {
        gameSaved = false
        gameState = GameState(gameSetup)
        dialogManager.closeDialog()
        update()
    }

    fun onModifierToggled(modifier: PerDartNumberPad.Modifier) {
        viewModelScope.launch(dispatcher) {
            val perDartNumberPad = numberPad as PerDartNumberPad
            perDartNumberPad.toggleModifier(modifier)
            update()
        }
    }

    fun createLegFinishedDialogViewModel() : SoloGameFinishedDialogViewModel {
        return SoloGameFinishedDialogViewModel(navigationManager, finishedLegDao, settingsRepository,
            this)
    }

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }

}