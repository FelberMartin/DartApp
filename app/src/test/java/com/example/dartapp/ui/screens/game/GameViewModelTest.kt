package com.example.dartapp.ui.screens.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dartapp.MainCoroutineRule
import com.example.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.getOrAwaitValueTest
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.game.testutil.PerDartNumPadEnter
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class GameViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        settingsRepository = SettingsRepository(InMemoryKeyValueStorage())
        viewModel = GameViewModel(NavigationManager(), settingsRepository)
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

    @Test
    fun `enter serve within finish range, show double attempts dialog`() {
        enterServes(listOf(180, 180, 100))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `enter serve within finish range with ask for double disabled, do not show double attempts dialog`() {
        runBlocking {
            settingsRepository.setAskForDouble(false)
            enterServes(listOf(180, 180, 100))
            val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().doubleAttemptsDialogOpen
            assertThat(showDialog).isFalse()
        }
    }

    @Test
    fun `enter dart within finish range, show simple double attempts dialog`() {
        enterServes(listOf(180, 180))
        enterDart(PerDartNumPadEnter(20, triple = true))
        enterDart(PerDartNumPadEnter(20, triple = true))
        enterDart(PerDartNumPadEnter(10))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().simpleDoubleAttemptsDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `enter last dart, do not show simple double attempts dialog`() {
        enterServes(listOf(180, 180))
        enterDart(PerDartNumPadEnter(20, triple = true))
        enterDart(PerDartNumPadEnter(20, triple = true))
        enterDart(PerDartNumPadEnter(15, double = true))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().simpleDoubleAttemptsDialogOpen
        assertThat(showDialog).isFalse()
    }

    @Test
    fun `enter double attempts, get added to game list`() {
        enterServes(listOf(180, 180, 141))
        viewModel.doubleAttemptsEntered(2)
        val doubleAttempts = viewModel.game.doubleAttempts
        assertThat(doubleAttempts).isEqualTo(2)
    }

    // ------------------ Checkout ----------------------------

    @Test
    fun `enter last serve, show checkout dialog`() {
        enterServes(listOf(180, 180, 141))
        val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().checkoutDialogOpen
        assertThat(showDialog).isTrue()
    }

    @Test
    fun `enter last serve with ask checkout disabled, do not show checkout dialog`() {
        runBlocking {
            settingsRepository.setAskForCheckout(false)
            enterServes(listOf(180, 180, 141))
            val showDialog = viewModel.dialogUiState.getOrAwaitValueTest().checkoutDialogOpen
            assertThat(showDialog).isFalse()
        }
    }

    @Test
    fun `enter single checkout dart, do only count one dart for serve`() {
        enterServes(listOf(180, 180, 139))
        enterServe(2)
        val dartCount = viewModel.dartCount.getOrAwaitValueTest()
        assertThat(dartCount).isEqualTo(10)
    }





    // ++++++++++++++ Utility ++++++++++++++++++++++

    private fun enterServes(serves: List<Int>)  {
        for (serve in serves) {
            enterServe(serve)
        }
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