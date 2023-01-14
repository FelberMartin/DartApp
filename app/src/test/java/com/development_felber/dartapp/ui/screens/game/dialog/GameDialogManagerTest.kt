package com.development_felber.dartapp.ui.screens.game.dialog

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test


internal class GameDialogManagerTest {

    lateinit var gameDialogManager: GameDialogManager

    @Before
    fun setUp() {
        gameDialogManager = GameDialogManager()
    }

    @Test
    fun `current dialog is null if no dialog is open`() {
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isNull()
    }

    @Test
    fun `open single dialog, current dialog is that dialog`() {
        gameDialogManager.openDialog(GameDialogManager.DialogType.AskForDouble)
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isEqualTo(GameDialogManager.DialogType.AskForDouble)
    }

    @Test
    fun `open multiple dialogs, current dialog is the highest priority dialog`() {
        gameDialogManager.openDialog(GameDialogManager.DialogType.LegJustFinished)
        gameDialogManager.openDialog(GameDialogManager.DialogType.AskForDoubleAndCheckout)
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isEqualTo(GameDialogManager.DialogType.AskForDoubleAndCheckout)
    }

    @Test
    fun `open multiple dialogs, close one dialog, current dialog is the highest priority dialog`() {
        gameDialogManager.openDialog(GameDialogManager.DialogType.LegJustFinished)
        gameDialogManager.openDialog(GameDialogManager.DialogType.AskForDoubleAndCheckout)
        gameDialogManager.closeDialog()
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isEqualTo(GameDialogManager.DialogType.LegJustFinished)
    }

    @Test
    fun `open multiple dialogs, close all dialogs, current dialog is null`() {
        gameDialogManager.openDialog(GameDialogManager.DialogType.LegJustFinished)
        gameDialogManager.openDialog(GameDialogManager.DialogType.AskForDoubleAndCheckout)
        gameDialogManager.closeDialog()
        gameDialogManager.closeDialog()
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isNull()
    }

    @Test
    fun `open multiple dialogs, close destructive dialog, current dialog is null`() {
        gameDialogManager.openDialog(GameDialogManager.DialogType.GameFinished)
        gameDialogManager.openDialog(GameDialogManager.DialogType.LegJustFinished)
        gameDialogManager.closeDialog()
        val nextDialog = gameDialogManager.currentDialog.value
        assertThat(nextDialog).isNull()
    }

}

