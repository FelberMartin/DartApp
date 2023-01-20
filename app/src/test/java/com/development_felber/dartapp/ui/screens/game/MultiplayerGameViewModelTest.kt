package com.development_felber.dartapp.ui.screens.game

import com.development_felber.dartapp.data.persistent.database.player.Player
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.PlayerRole
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.screens.game.dialog.GameDialogManager
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialogResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MultiplayerGameViewModelTest : GameViewModelTest() {

    private val player1 =  Player("Player 1")
    private val player2 = Player("Player 2")

    @Before
    override fun setup() {
        setupGameViewModel(gameSetup = GameSetup.Multiplayer(
            player1 = player1,
            player2 =  player2,
            setsToWin = 3,
            legsToWinSet = 3,
        ))
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

    @Test
    fun `after finishing leg, it is added to previous legs list`() = runHotFlowTest {
        endGame()
        val previousLegs = viewModel.gameState.currentPlayerGameState.previousLegs
        assertThat(previousLegs).hasSize(1)
    }

    private suspend fun endGame() {
        enterServes(listOf(180, 0, 180, 0, 141))
        delay(1)
        viewModel.doubleAttemptsAndCheckoutConfirmed(1, 3)
        viewModel.onContinueInDialogClicked()
    }

    @Test
    fun `rotate players after each leg`() = runHotFlowTest {
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.One)
        endGame()
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.Two)
        endGame()
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.One)
    }

    // ---------- Saving to Database -------------

    @Test
    fun `after finishing a game with only one leg to win, a leg gets stored for each player`() = runTest {
        setupGameViewModel(gameSetup = GameSetup.Multiplayer(
            player1 = player1,
            player2 = player2,
            legsToWinSet = 1,
            setsToWin = 1,
        ))
        endGame()
        delay(1)
        val legs = finishedLegDao.getAllLegs().getOrAwaitValueTest()
        val player1Leg = legs.firstOrNull { it.playerName == player1.name }
        assertThat(player1Leg).isNotNull()
        val player2Leg = legs.firstOrNull { it.playerName == player2.name }
        assertThat(player2Leg).isNotNull()
    }

    @Test
    fun `do not save legs, if the game is still in progress`() = runTest {
        endGame()
        delay(1)
        val savedLegs = finishedLegDao.getAllLegs().getOrAwaitValueTest()
        assertThat(savedLegs).isEmpty()
    }

    // Save Sets and Multiplayer entries
}