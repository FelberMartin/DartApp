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

    companion object {
        private val player1 =  Player("Player 1")
        private val player2 = Player("Player 2")
        private const val setsToWin = 3
        private const val legsToWinSet = 3
    }

    @Before
    override fun setup() {
        setupGameViewModel(gameSetup = createGameSetup())
    }

    private fun createGameSetup(
        player1: Player = Companion.player1,
        player2: Player = Companion.player2,
        setsToWin: Int = Companion.setsToWin,
        legsToWinSet: Int = Companion.legsToWinSet,
    ) = GameSetup.Multiplayer(
        player1 = player1,
        player2 = player2,
        setsToWin = setsToWin,
        legsToWinSet = legsToWinSet,
    )


    // --------------- Dialogs -------------

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
        viewModel.onContinueToNextLegClicked()
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
    fun `after finishing set, show set finished dialog`() = runHotFlowTest {
        endSet(keepDialog = true)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.SetJustFinished)
    }

    @Test
    fun `after winning all sets, show game finished dialog`() = runHotFlowTest {
        endGame(keepDialog = true)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.GameFinished)
    }



    // ---------- Saving to Database -------------

    @Test
    fun `after finishing a game with only one leg to win, a leg gets stored for each player`() = runTest {
        setupGameViewModel(gameSetup = createGameSetup(
            legsToWinSet = 1,
            setsToWin = 1,
        ))
        endLeg()
        delay(1)
        val legs = finishedLegDao.getAllLegs().getOrAwaitValueTest()
        val player1Leg = legs.firstOrNull { it.playerName == player1.name }
        assertThat(player1Leg).isNotNull()
        val player2Leg = legs.firstOrNull { it.playerName == player2.name }
        assertThat(player2Leg).isNotNull()
    }

    @Test
    fun `do not save legs, if the game is still in progress`() = runTest {
        endLeg()
        delay(1)
        val savedLegs = finishedLegDao.getAllLegs().getOrAwaitValueTest()
        assertThat(savedLegs).isEmpty()
    }

    @Test
    fun `after finishing a game, save the game`() = runTest {
        endGame()
        val savedGames = multiplayerGameDao.getAll()
        assertThat(savedGames).hasSize(1)
    }

    @Test
    fun `after finishing a game, save the sets`() = runTest {
        endGame()
        val savedSets = dartSetDao.getAll()
        assertThat(savedSets).hasSize(setsToWin)
    }

    @Test
    fun `saved sets have distinct leg ids`() = runTest {
        endGame()
        val savedSets = dartSetDao.getAll()
        val legIds = savedSets.map { it.legIds }.flatten()
        assertThat(legIds).containsNoDuplicates()
    }



    // ------------ Others ---------------

    @Test
    fun `after finishing leg, it is added to previous legs list`() = runHotFlowTest {
        endLeg()
        val previousLegs = viewModel.gameState.currentPlayerGameState.previousLegsPerSet.flatten()
        assertThat(previousLegs).hasSize(1)
    }

    @Test
    fun `rotate players after each leg`() = runHotFlowTest {
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.One)
        endLeg()
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.Two)
        endLeg()
        assertThat(viewModel.gameState.currentPlayerRole).isEqualTo(PlayerRole.One)
    }

    @Test
    fun `after finishing the first leg, show the right minDarts requirement in double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 141))
        delay(1)
        val minDarts = viewModel.getMinimumDartCount()
        assertThat(minDarts).isEqualTo(3)
    }

    @Test
    fun `entered double attempts without finish, get applied to right leg`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 100))
        viewModel.doubleAttemptsAndCheckoutConfirmed(
            DoubleAttemptsAndCheckoutDialogResult(1, null)
        )
        delay(1)
        val player1PlayerGameState = viewModel.gameState.playerGameStates.first { it.playerRole == PlayerRole.One }
        val leg = player1PlayerGameState.currentLeg
        assertThat(leg.doubleAttempts).isEqualTo(1)
    }

    @Test
    fun `entered double attempts and checkout, get applied to the right leg`() = runHotFlowTest {
        enterServes(listOf(180, 0, 180, 0, 100))
        viewModel.doubleAttemptsAndCheckoutConfirmed(1, null)
        delay(1)
        enterServes(listOf(0, 41))
        viewModel.doubleAttemptsAndCheckoutConfirmed(
            DoubleAttemptsAndCheckoutDialogResult(1, 2)
        )
        delay(1)
        viewModel.onContinueToNextLegClicked()
        delay(1)
        val player1PlayerGameState = viewModel.gameState.playerGameStates.first { it.playerRole == PlayerRole.One }
        val player1Leg = player1PlayerGameState.previousLegsPerSet.flatten().first()
        assertThat(player1Leg.doubleAttempts).isEqualTo(2)
        assertThat(player1Leg.dartCount).isEqualTo(3 * 3 + 2)
    }


    // -------------- Helpers ---------------

    private suspend fun endLeg(
        winner: PlayerRole = PlayerRole.One,
        keepDialog: Boolean = false,
        doubleAndCheckout: DoubleAttemptsAndCheckoutDialogResult =
            DoubleAttemptsAndCheckoutDialogResult(1, 3)
    ) {
        val serves = mutableListOf(180, 0, 180, 0, 141)
        val startingPlayer = viewModel.gameState.currentPlayerRole
        if (startingPlayer != winner) {
            serves.add(0, 0)
        }
        enterServes(serves)
        delay(1)
        viewModel.doubleAttemptsAndCheckoutConfirmed(doubleAndCheckout)
        if (!keepDialog) {
            viewModel.onContinueToNextLegClicked()
        }
    }

    private fun endSet(winner: PlayerRole = PlayerRole.One, keepDialog: Boolean = false) = runTest {
        repeat(legsToWinSet) {
            val lastRound = it == (legsToWinSet - 1)
            endLeg(winner = winner, keepDialog = lastRound && keepDialog)
            delay(1)
        }
    }

    private fun endGame(winner: PlayerRole = PlayerRole.One, keepDialog: Boolean = false) = runTest {
        repeat(setsToWin) {
            val lastRound = it == legsToWinSet - 1
            endSet(winner = winner, keepDialog = lastRound && keepDialog)
            delay(1)
        }
    }
}