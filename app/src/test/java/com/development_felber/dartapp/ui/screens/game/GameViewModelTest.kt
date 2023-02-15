@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)

package com.development_felber.dartapp.ui.screens.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.development_felber.dartapp.MainCoroutineScopeRule
import com.development_felber.dartapp.data.persistent.database.dart_set.DartSetDao
import com.development_felber.dartapp.data.persistent.database.dart_set.FakeDartSetDao
import com.development_felber.dartapp.data.persistent.database.finished_leg.FakeFinishedLegDao
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.FakeMultiplayerGameDao
import com.development_felber.dartapp.data.persistent.database.multiplayer_game.MultiplayerGameDao
import com.development_felber.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.development_felber.dartapp.data.repository.GameRepository
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.game.numberpad.PerDartNumberPad
import com.development_felber.dartapp.game.numberpad.PerServeNumberPad
import com.development_felber.dartapp.ui.navigation.GameSetupHolder
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.testutil.PerDartNumPadEnter
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
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

    protected var settingsRepository: SettingsRepository = SettingsRepository(InMemoryKeyValueStorage())
    protected var finishedLegDao: FinishedLegDao = FakeFinishedLegDao()
    protected var dartSetDao: DartSetDao = FakeDartSetDao()
    protected var multiplayerGameDao: MultiplayerGameDao = FakeMultiplayerGameDao()
    protected var gameRepository: GameRepository = GameRepository(finishedLegDao, dartSetDao, multiplayerGameDao)
    protected lateinit var viewModel: GameViewModel
    private lateinit var collectJob: Job

    @Before
    open fun setup() {
        setupGameViewModel()
    }

    protected fun setupGameViewModel(
        gameSetup: GameSetup = GameSetup.Solo
    ) {
        GameSetupHolder.gameSetup = gameSetup
        viewModel = GameViewModel(NavigationManager(), settingsRepository, gameRepository, finishedLegDao, coroutineRule.dispatcher)
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



