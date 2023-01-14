package com.development_felber.dartapp.ui.screens.game.dialog

import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameState
import com.development_felber.dartapp.game.GameStatus
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.util.CheckoutTip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class GameDialogManager {

    enum class DialogType {
        AskForDouble,
        AskForDoubleSimple,
        AskForCheckout,
        AskForDoubleAndCheckoutCombined,
        LegJustFinished,
        SetJustFinished,
        GameFinished,
        ExitGame
    }

    private val hierarchy = listOf(
        DialogType.ExitGame,
        DialogType.AskForDoubleSimple,
        DialogType.AskForDouble,
        DialogType.AskForCheckout,
        DialogType.AskForDoubleAndCheckoutCombined,
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


    fun openDialog(dialogType: DialogType) {
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
        updateCurrentDialog()
    }

    private fun updateCurrentDialog() {
        val nextDialog = dialogsToOpen.minByOrNull { hierarchy.indexOf(it) }
        _currentDialog.value = nextDialog
    }

    suspend fun determineDialogsToOpen(
        gameState: GameState,
        lastNumberEntered: Int,
        usePerDartNumberPad: Boolean,
        settingsRepository: SettingsRepository,
    ) {
        if (usePerDartNumberPad) {
            if (shouldShowSimpleDoubleAttemptDialog(
                lastDart = lastNumberEntered,
                gameState = gameState,
                settingsRepository = settingsRepository,
            )) {
                openDialog(DialogType.AskForDoubleSimple)
            }
        } else {
            val checkout = shouldShowCheckoutDialog(
                gameState = gameState,
                settingsRepository = settingsRepository,
            )
            val double = shouldShowDoubleAttemptsDialog(
                lastServe = lastNumberEntered,
                gameState = gameState,
                settingsRepository = settingsRepository,
            )
            when {
                checkout && double -> openDialog(DialogType.AskForDoubleAndCheckoutCombined)
                checkout -> openDialog(DialogType.AskForCheckout)
                double -> openDialog(DialogType.AskForDouble)
            }
        }

        when (gameState.gameStatus) {
            GameStatus.LegJustFinished -> openDialog(DialogType.LegJustFinished)
            GameStatus.SetJustFinished -> openDialog(DialogType.SetJustFinished)
            GameStatus.Finished -> openDialog(DialogType.GameFinished)
            else -> { /* Do nothing */ }
        }
    }

    private suspend fun shouldShowSimpleDoubleAttemptDialog(
        lastDart: Int,
        settingsRepository: SettingsRepository,
        gameState: GameState,
    ) : Boolean {
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

    private suspend fun shouldShowDoubleAttemptsDialog(
        lastServe: Int,
        settingsRepository: SettingsRepository,
        gameState: GameState,
    ) : Boolean {
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

    private suspend fun shouldShowCheckoutDialog(
        settingsRepository: SettingsRepository,
        gameState: GameState,
    ) : Boolean {
        val askForCheckout = settingsRepository
            .getBooleanSettingFlow(SettingsRepository.BooleanSetting.AskForCheckout).first()
        if (!askForCheckout) {
            return false
        }
        return gameState.currentLeg.pointsLeft == 0
    }

}