package com.development_felber.dartapp.ui.screens.game.dialog

import android.util.Log
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameState
import com.development_felber.dartapp.game.GameStatus
import com.development_felber.dartapp.util.CheckoutTip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class GameDialogManager (
    private val settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope,
) {

    sealed class DialogType {
        class AskForDoubleAndOrCheckout(
            val askForDouble: Boolean,
            val askForCheckout: Boolean
        ) : DialogType() {
            override fun equals(other: Any?): Boolean {
                return other is AskForDoubleAndOrCheckout
            }
        }

        object AskForDoubleSimple : DialogType()
        object LegJustFinished : DialogType()
        object SetJustFinished : DialogType()
        object GameFinished : DialogType()
        object ExitGame : DialogType()
    }

    private val hierarchy = listOf(
        DialogType.ExitGame,
        DialogType.AskForDoubleSimple,
        DialogType.AskForDoubleAndOrCheckout(true, true),
        DialogType.GameFinished,
        DialogType.SetJustFinished,
        DialogType.LegJustFinished,
    )

    private val destructiveDialogs = listOf(
        DialogType.ExitGame,
        DialogType.GameFinished,
    )

    private val dialogsToOpen = mutableSetOf<DialogType>()

    private val _currentDialog = MutableStateFlow<DialogType?>(null)
    val currentDialog = _currentDialog.asStateFlow()

    private val askForDoubleSetting = settingsRepository
        .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForDouble)
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)

    private val askForCheckoutSetting = settingsRepository
        .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForCheckout)
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), false)


    fun openDialog(dialogType: DialogType) {
        Log.d("GameDialogManager", "openDialog: $dialogType")
        dialogsToOpen.add(dialogType)
        updateCurrentDialog()
    }

    fun closeDialog() {
        val currentDialog = currentDialog.value ?: return
        if (currentDialog in destructiveDialogs) {
            dialogsToOpen.clear()
        } else {
            dialogsToOpen.remove(currentDialog)
        }
        Log.d("GameDialogManager", "closeDialog: $currentDialog")
        updateCurrentDialog()
    }

    private fun updateCurrentDialog() {
        val nextDialog = dialogsToOpen.minByOrNull { hierarchy.indexOf(it) }
        Log.d("GameDialogManager", "updateCurrentDialog: $nextDialog")
        _currentDialog.value = nextDialog
    }

    fun determineDialogsToOpen(
        gameState: GameState,
        lastNumberEntered: Int,
        usePerDartNumberPad: Boolean,
    ) {
        if (usePerDartNumberPad) {
            if (shouldShowSimpleDoubleAttemptDialog(
                lastDart = lastNumberEntered,
                gameState = gameState,
            )) {
                openDialog(DialogType.AskForDoubleSimple)
            }
        } else {
            val checkout = shouldShowCheckoutDialog(
                gameState = gameState,
            )
            val double = shouldShowDoubleAttemptsDialog(
                lastServe = lastNumberEntered,
                gameState = gameState,
            )
            if (checkout || double) {
                openDialog(DialogType.AskForDoubleAndOrCheckout(double, checkout))
            }
        }

        when (gameState.gameStatus) {
            is GameStatus.LegJustFinished -> openDialog(DialogType.LegJustFinished)
            is GameStatus.SetJustFinished -> openDialog(DialogType.SetJustFinished)
            GameStatus.Finished -> openDialog(DialogType.GameFinished)
            else -> { /* Do nothing */ }
        }
    }

    private fun shouldShowSimpleDoubleAttemptDialog(
        lastDart: Int,
        gameState: GameState,
    ) : Boolean {
        if (!askForDoubleSetting.value) {
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

    private fun shouldShowDoubleAttemptsDialog(
        lastServe: Int,
        gameState: GameState,
    ) : Boolean {
        if (!askForDoubleSetting.value) {
            return false
        }
        val leg = gameState.currentLeg
        if (leg.pointsLeft > 50 && lastServe > 0) {
            return false
        }
        val pointsBeforeServe = leg.pointsLeft + lastServe
        return CheckoutTip.checkoutTips.contains(pointsBeforeServe)
    }

    private fun shouldShowCheckoutDialog(
        gameState: GameState,
    ) : Boolean {
        if (!askForCheckoutSetting.value) {
            return false
        }
        return gameState.currentLeg.pointsLeft == 0
    }

}