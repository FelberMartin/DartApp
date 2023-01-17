@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)

package com.development_felber.dartapp.ui.screens.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.core.app.ActivityScenario.launch
import com.development_felber.dartapp.MainCoroutineScopeRule
import com.development_felber.dartapp.data.persistent.database.finished_leg.FakeFinishedLegDao
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.game.numberpad.PerServeNumberPad
import com.development_felber.dartapp.getOrAwaitValueTest
import com.development_felber.dartapp.ui.navigation.GameSetupHolder
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.dialog.GameDialogManager
import com.development_felber.dartapp.ui.screens.game.testutil.PerDartNumPadEnter
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Integer.min

open class GameViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineScopeRule()

    protected lateinit var settingsRepository: SettingsRepository
    protected lateinit var finishedLegDao: FinishedLegDao
    protected lateinit var viewModel: GameViewModel

    protected var gameSetup: GameSetup = GameSetup.Solo
    private lateinit var collectJob: Job

    @Before
    fun setup() {
        settingsRepository = SettingsRepository(InMemoryKeyValueStorage())
        finishedLegDao = FakeFinishedLegDao()
        GameSetupHolder.gameSetup = gameSetup
        viewModel = GameViewModel(NavigationManager(), settingsRepository, finishedLegDao, coroutineRule.dispatcher)
    }

    private val numberPad
        get() = viewModel.gameUiState.value.numberPadUiState.numberPad


    @Test
    fun `swap number pad works as expected`() = runHotFlowTest {
        assertThat(numberPad).isInstanceOf(PerServeNumberPad::class.java)
        viewModel.onSwapNumberPadClicked()
        delay(1)
        assertThat(numberPad).isInstanceOf(PerDartNumberPad::class.java)
        viewModel.onSwapNumberPadClicked()
        delay(1)
        assertThat(numberPad).isInstanceOf(PerServeNumberPad::class.java)
    }

    protected fun runHotFlowTest(block: suspend TestScope.() -> Unit) {
        runTest {
            collectHotFlows()
            block()
            closeHotFlows()
        }
    }

    protected fun TestScope.collectHotFlows() {
        // Create an empty collector for the StateFlow
        collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.gameUiState.collect()
        }
    }

    protected fun TestScope.closeHotFlows() {
        // Cancel the collecting job at the end of the test
        collectJob.cancel()
    }

    // ++++++++++++++ Utility ++++++++++++++++++++++

    fun enterServes(serves: List<Int>)  {
        for (serve in serves) {
            enterServe(serve)
        }
    }

    fun generateServesTillAt(wantedScore: Int): List<Int> {
        var neededPoints = 501 - wantedScore
        val serves = mutableListOf<Int>()
        while (neededPoints > 0) {
            val serve = min(180, neededPoints)
            serves.add(serve)
            neededPoints -= serve
        }
        return serves
    }

    fun enterServe(serve: Int) {
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

    suspend fun enterDart(dart: PerDartNumPadEnter) {
        swapIfNeeded(true)
        delay(1)
        dart.apply(viewModel)
    }
}

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
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble , false)
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
        viewModel.enterDoubleAttempts(2)
        val doubleAttempts = viewModel.gameState.currentLeg.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    @Test
    fun `enter double attempts, gets saved to database`() = runTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        viewModel.enterDoubleAttempts(2)
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
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog.showsCheckoutDialog()).isFalse()
    }

    @Test
    fun `enter single checkout dart, do only count one dart for serve`() = runHotFlowTest {
        enterServes(listOf(180, 180, 139))
        enterServe(2)
        viewModel.enterCheckoutDarts(1)
        delay(1)
        val dartCount = player.dartCount
        assertThat(dartCount).isEqualTo(10)
    }


    // ---------- Leg Finished ---------------

    @Test
    fun `enter finish serve with all other dialogs disabled, show game finished dialog`() = runHotFlowTest {
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForDouble , false)
        settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.AskForCheckout , false)
        enterServes(listOf(180, 180, 141))
        delay(1)
        val dialog = viewModel.gameUiState.value.dialogToShow
        assertThat(dialog).isEqualTo(GameDialogManager.DialogType.GameFinished)
    }

    // -------------- Enter button enablement -----------------
    @Test
    fun `want to enter zero in finish range, enter button is enabled`() = runHotFlowTest {
        enterServe(180)
        enterServe(180)
        delay(1 )
        val enabled = viewModel.gameUiState.value.numberPadUiState.enterEnabled
        assertThat(enabled).isTrue()
    }

}

