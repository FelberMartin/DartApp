package com.development_felber.dartapp.ui.screens.game

import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.ui.screens.game.dialog.GameDialogManager
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialogResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MultiplayerGameViewModelTest : GameViewModelTest() {

    init {
        gameSetup = GameSetup.Multiplayer(
            player1 =  Player("Player 1"),
            player2 =  Player("Player 2"),
            setsToWin = 3,
            legsToWinSet = 3,
        )
    }

    @Test
    fun `player one finishes leg, show LegWonDialog after entering double and checkout`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 141))
        viewModel.doubleAttemptsAndCheckoutConfirmed(
            DoubleAttemptsAndCheckoutDialogResult(1, 3)
        )
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.LegJustFinished)
    }

    @Test
    fun `after finishing the first leg and dismissing the dialog, do not show the dialog again immediately`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 141))
        viewModel.doubleAttemptsAndCheckoutConfirmed(
            DoubleAttemptsAndCheckoutDialogResult(1, 3)
        )
        viewModel.onContinueInDialogClicked()
        enterServes(listOf(21, 42))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isNull()
    }

    @Test
    fun `ask for double attempts and checkout after finishing the first leg`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isInstanceOf(GameDialogManager.DialogType.AskForDoubleAndOrCheckout::class.java)
    }
    
    @Test
    fun `player 1 about to finish leg, do not ask player 2 for double attempts`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isNull()
    }
}