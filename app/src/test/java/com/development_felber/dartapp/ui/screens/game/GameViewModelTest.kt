@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.development_felber.dartapp.ui.screens.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.development_felber.dartapp.MainCoroutineRule
import com.development_felber.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.development_felber.dartapp.data.persistent.database.LegDatabaseDao
import com.development_felber.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.testutil.PerDartNumPadEnter
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.lang.Integer.min

class GameViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var legDatabaseDao: LegDatabaseDao
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        settingsRepository = SettingsRepository(InMemoryKeyValueStorage())
        legDatabaseDao = FakeLegDatabaseDao()
        viewModel = GameViewModel(NavigationManager(), settingsRepository, legDatabaseDao)
    }


    @Test
    fun `swap to serve numpad after entering a single dart, add remaining two darts`() {
        enterDart(PerDartNumPadEnter(10))
        viewModel.onSwapNumberPadClicked()
        val dartCount = viewModel.dartCount.getOrAwaitValueTest()
        assertThat(dartCount).isEqualTo(3)
    }


    // ------------ Average ---------------------

    @Test
    fun `enter one number, correct average calculated`() {
        enterServe(69)
        val avg = viewModel.average.getOrAwaitValueTest()
        assertThat(avg).isEqualTo("69.00")
    }

    @Test
    fun `swap numpad, shows correct average`() {
        enterServe(9)
        viewModel.onSwapNumberPadClicked()
        val avg = viewModel.average.getOrAwaitValueTest()
        assertThat(avg).isEqualTo("3.00")
    }


    // ------------ Double Attempts --------------

    // ~~~~~~ Show Dialog ~~~~~~~
    @Test
    fun `enter serve within finish range, show double attempts dialog`() = runTest {
        enterServes(listOf(180, 180, 100))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `finish with serve, show double attempts dialog`() = runTest {
        enterServes(generateServesTillAt(60))
        enterServe(60)
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `finish with serve, show double attempts dialog, version 2`() = runTest {
        enterServes(generateServesTillAt(41))
        enterServe(41)
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `in finish range and enter zero, show double attempts dialog`() = runTest {
        enterServes(listOf(180, 180))                                       // 141 left
        enterServe(0)
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `enter dart within finish range, show simple double attempts dialog`() = runTest {
        enterServes(listOf(180, 180))                                       // 141 left
        enterDart(PerDartNumPadEnter(20, PerDartNumberPad.Modifier.Triple))        // 81
        enterDart(PerDartNumPadEnter(19, PerDartNumberPad.Modifier.Triple))        // 24
        enterDart(PerDartNumPadEnter(12))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().simpleDoubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    // ~~~~~~ Do NOT Show Dialog ~~~~~~~

    @Test
    fun `enter serve within finish range with ask for double disabled, do not show double attempts dialog`() = runTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble , false)
        enterServes(listOf(180, 180, 100))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isFalse()
    }

    @Test
    fun `enter serve outside finish range, do not show double attempts dialog`() = runTest {
        enterServes(listOf(180, 180))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isFalse()
    }

    @Test
    fun `enter serve in finish range, but failed in between, do not show double attempts dialog`() = runTest {
        enterServes(listOf(180, 180, 41))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isFalse()
    }

    @Test
    fun `enter last dart, do not show simple double attempts dialog`() = runTest {
        enterServes(listOf(180, 180))
        enterDart(PerDartNumPadEnter(20, PerDartNumberPad.Modifier.Triple))
        enterDart(PerDartNumPadEnter(19, PerDartNumberPad.Modifier.Triple))
        enterDart(PerDartNumPadEnter(12, PerDartNumberPad.Modifier.Double))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().simpleDoubleAttemptsDialogOpen
        assertThat(showDialog).isFalse()
    }

    // ~~~~~~ Misc ~~~~~~~

    @Test
    fun `enter double attempts, get added to game list`() {
        enterServes(listOf(180, 180, 141))
        viewModel.enterDoubleAttempts(2)
        val doubleAttempts = viewModel.game.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    @Test
    fun `enter double attempts, gets saved to database`() = runTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        viewModel.enterDoubleAttempts(2)
        val doubleAttempts = legDatabaseDao.getLatestLeg()?.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    // ------------------ Checkout ----------------------------

    @Test
    fun `enter last serve, show checkout dialog`() = runTest {
        enterServes(listOf(180, 180, 141))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().checkoutDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `enter last serve with ask checkout disabled, do not show checkout dialog`() = runTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().checkoutDialogOpen
        assertThat(showDialog).isFalse()
    }

    @Test
    fun `enter single checkout dart, do only count one dart for serve`() {
        enterServes(listOf(180, 180, 139))
        enterServe(2)
        viewModel.enterCheckoutDarts(1)
        val dartCount = viewModel.dartCount.getOrAwaitValueTest()
        assertThat(dartCount).isEqualTo(10)
    }


    // ---------- Leg Finished ---------------

    @Ignore
    @Test
    fun `enter finish serve, show leg finished dialog`() = runTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble , false)
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        val showDialog = viewModel.legFinished.getOrAwaitValueTest()
        assertThat(showDialog).isTrue()
    }

    @Ignore
    @Test
    fun `enter finish serve with all other dialogs disabled, show leg finished dialog`() {
        runTest {
            settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble , false)
            settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
            enterServes(listOf(180, 180, 141))
            val showDialog = viewModel.legFinished.getOrAwaitValueTest()
            assertThat(showDialog).isTrue()
        }
    }

    // -------------- Enter button enablement -----------------
    @Test
    fun `want to enter zero in finish range, enter button is enabled`() {
        enterServe(180)
        enterServe(180)
        val disabled = viewModel.enterDisabled.getOrAwaitValueTest()
        assertThat(disabled).isFalse()
    }


    // ++++++++++++++ Utility ++++++++++++++++++++++

    private fun enterServes(serves: List<Int>)  {
        for (serve in serves) {
            enterServe(serve)
        }
    }

    private fun generateServesTillAt(wantedScore: Int): List<Int> {
        var neededPoints = 501 - wantedScore
        val serves = mutableListOf<Int>()
        while (neededPoints > 0) {
            val serve = min(180, neededPoints)
            serves.add(serve)
            neededPoints -= serve
        }
        return serves
    }

    private fun enterServe(serve: Int) {
        swapIfNeeded(false)
        val string = serve.toString()
        for (char in string.toCharArray()) {
            viewModel.onNumberTyped(char.digitToInt())
        }
        viewModel.onEnterClicked()
    }

    private fun swapIfNeeded(onPerDartNumberPad: Boolean) {
        val swap = viewModel.usePerDartNumberPad != onPerDartNumberPad
        if (swap) {
            viewModel.onSwapNumberPadClicked()
        }
    }

    private fun enterDart(dart: PerDartNumPadEnter) {
        swapIfNeeded(true)
        dart.apply(viewModel)
    }
}