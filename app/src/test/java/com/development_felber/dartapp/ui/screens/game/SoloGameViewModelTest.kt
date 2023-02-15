package com.development_felber.dartapp.ui.screens.game

import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.screens.game.dialog.GameDialogManager
import com.development_felber.dartapp.ui.screens.game.dialog.during_leg.DoubleAttemptsAndCheckoutDialogResult
import com.development_felber.dartapp.ui.screens.game.testutil.PerDartNumPadEnter
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SoloGameViewModelTest : GameViewModelTest() {

    private val player
        get() = viewModel.gameUiState.value.playerUiStates[0]

    @Test
    fun `swap to serve numpad after entering a single dart, add remaining two darts`() = runTest {
        enterDart(PerDartNumPadEnter(10))
        viewModel.onSwapNumberPadClicked()
        delay(1)    // Wait for the state to be updated
        val dartCount = viewModel.gameUiState.value.playerUiStates[0].dartCount
//        val dartCount = viewModel.gameUiState.asLiveData().getOrAwaitValueTest().playerUiStates[0].dartCount
        assertThat(dartCount).isEqualTo(3)
    }


    // ------------ Average ---------------------

    @Test
    fun `enter one number, correct average calculated`() = runHotFlowTest {
        enterServe(69)
        delay(1)
        val avg = player.average
        assertThat(avg).isEqualTo(69.0)
    }

    @Test
    fun `swap numpad, shows correct average`() = runHotFlowTest {
        enterServe(9)
        viewModel.onSwapNumberPadClicked()
        delay(1)
        val avg = player.average
        assertThat(avg).isEqualTo(3.0)
    }


    // ------------ Double Attempts --------------

    // ~~~~~~ Show Dialog ~~~~~~~
    @Test
    fun `enter serve within finish range, show double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180, 100))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isTrue()
    }

    private fun GameDialogManager.DialogType?.showsDoubleDialog() : Boolean {
        return when (this) {
            is GameDialogManager.DialogType.AskForDoubleAndOrCheckout -> askForDouble
            else -> false
        }
    }

    @Test
    fun `finish with serve, show double attempts dialog`() = runHotFlowTest {
        enterServes(generateServesTillAt(60))
        enterServe(60)
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isTrue()
    }

    @Test
    fun `finish with serve, show double attempts dialog, version 2`() = runHotFlowTest {
        enterServes(generateServesTillAt(41))
        enterServe(41)
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isTrue()
    }

    @Test
    fun `in finish range and enter zero, show double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180))                                       // 141 left
        enterServe(0)
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isTrue()
    }

    @Test
    fun `enter dart within finish range, show simple double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180))                                       // 141 left
        enterDart(PerDartNumPadEnter(20, PerDartNumberPad.Modifier.Triple))        // 81
        enterDart(PerDartNumPadEnter(19, PerDartNumberPad.Modifier.Triple))        // 24
        enterDart(PerDartNumPadEnter(12))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.AskForDoubleSimple)
    }

    // ~~~~~~ Do NOT Show Dialog ~~~~~~~

    @Test
    fun `enter serve within finish range with ask for double disabled, do not show double attempts dialog`() = runHotFlowTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble, false)
        enterServes(listOf(180, 180, 100))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isFalse()
    }

    @Test
    fun `enter serve outside finish range, do not show double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isFalse()
    }

    @Test
    fun `enter serve in finish range, but failed in between, do not show double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180, 41))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsDoubleDialog()).isFalse()
    }

    @Test
    fun `enter last dart, do not show simple double attempts dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180))
        enterDart(PerDartNumPadEnter(20, PerDartNumberPad.Modifier.Triple))
        enterDart(PerDartNumPadEnter(19, PerDartNumberPad.Modifier.Triple))
        enterDart(PerDartNumPadEnter(12, PerDartNumberPad.Modifier.Double))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isNotInstanceOf(GameDialogManager.DialogType.AskForDoubleSimple::class.java)
    }

    // ~~~~~~ Misc ~~~~~~~

    @Test
    fun `enter double attempts, get added to game list`() = runTest {
        enterServes(listOf(180, 180, 141))
        val result = DoubleAttemptsAndCheckoutDialogResult(doubleAttempts = 2)
        viewModel.doubleAttemptsAndCheckoutConfirmed(result)
        val doubleAttempts = viewModel.gameState.currentLeg.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    @Test
    fun `enter double attempts, gets saved to database`() = runTest {
        settingsRepository.setBooleanSetting(
            SettingsRepository.BooleanSetting.AskForCheckout,
            false
        )
        enterServes(listOf(180, 180, 141))
        val result = DoubleAttemptsAndCheckoutDialogResult(doubleAttempts = 2)
        viewModel.doubleAttemptsAndCheckoutConfirmed(result)
        delay(1)
        val doubleAttempts = finishedLegDao.getLatestLeg()?.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    // ------------------ Checkout ----------------------------

    @Test
    fun `enter last serve, show checkout dialog`() = runHotFlowTest {
        enterServes(listOf(180, 180, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsCheckoutDialog()).isTrue()
    }

    private fun GameDialogManager.DialogType?.showsCheckoutDialog() : Boolean {
        return when (this) {
            is GameDialogManager.DialogType.AskForDoubleAndOrCheckout -> askForCheckout
            else -> false
        }
    }

    @Test
    fun `enter last serve with ask checkout disabled, do not show checkout dialog`() = runHotFlowTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout, false)
        enterServes(listOf(180, 180, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsCheckoutDialog()).isFalse()
    }

    @Test
    fun `enter single checkout dart, do only count one dart for serve`() = runHotFlowTest {
        enterServes(listOf(180, 180, 139))
        enterServe(2)
        val result = DoubleAttemptsAndCheckoutDialogResult(checkout = 1)
        viewModel.doubleAttemptsAndCheckoutConfirmed(result)
        delay(1)
        val dartCount = player.dartCount
        assertThat(dartCount).isEqualTo(10)
    }


    // ---------- Leg Finished ---------------

    @Ignore("Due to an error with coroutines, which is probably a bug in the JDK. (https://github.com/Kotlin/kotlinx.coroutines/issues/1300)")
    @Test
    fun `enter finish serve with all other dialogs disabled, show game finished dialog`() = runHotFlowTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble, false)
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout, false)
        enterServes(listOf(180, 180, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.GameFinished)
    }

    @Test
    fun `after finishing game, leg gets saved to the database`() = runTest {
        // Make sure that there are no entries in the database before.
        assertThat(finishedLegDao.getLatestLeg()).isNull()
        enterServes(listOf(180, 180, 141))
        delay(1)
        viewModel.doubleAttemptsAndCheckoutConfirmed(1, 3)
        delay(1)
        val allLegs = finishedLegDao.getAllLegs().getOrAwaitValueTest()
        assertThat(allLegs.size).isEqualTo(1)
    }

    // -------------- Enter button enablement -----------------
    @Test
    fun `want to enter zero in finish range, enter button is enabled`() = runHotFlowTest {
        enterServe(180)
        enterServe(180)
        delay(1)
        val enabled = viewModel.gameUiState.value.numberPadUiState.enterEnabled
        assertThat(enabled).isTrue()
    }
}