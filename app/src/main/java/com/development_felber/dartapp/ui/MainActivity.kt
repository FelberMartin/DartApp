@file:OptIn(ExperimentalAnimationApi::class)

package com.development_felber.dartapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.development_felber.dartapp.data.AppearanceOption
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.ui.navigation.*
import com.development_felber.dartapp.ui.screens.game.GameScreen
import com.development_felber.dartapp.ui.screens.history.HistoryScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsViewModel
import com.development_felber.dartapp.ui.screens.home.HomeScreen
import com.development_felber.dartapp.ui.screens.home.dialogs.StartMultiplayerDialogViewModelEntryPoint
import com.development_felber.dartapp.ui.screens.settings.SettingsScreen
import com.development_felber.dartapp.ui.screens.splash.AnimatedSplashScreen
import com.development_felber.dartapp.ui.screens.statistics.StatisticsScreen
import com.development_felber.dartapp.ui.screens.table.TableScreen
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DartApp(
                navigationManager = navigationManager,
                settingsRepository = settingsRepository
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DartApp(
    settingsRepository: SettingsRepository,
    navigationManager: NavigationManager,
) {
    val appearanceOption = settingsRepository.appearanceOptionFlow.collectAsState(AppearanceOption.Default)
    val navController = rememberAnimatedNavController()

    DartAppTheme(
        useDarkTheme = appearanceOption.value.useDarkTheme(isSystemInDarkTheme())
    ) {
        SetupNavHost(navController)
    }

    // Collect navigation commands and execute them.
    navigationManager.commands.collectAsState().value.also { oneTimeCommand ->
        oneTimeCommand.use { command -> command.navigateWith(navController) }
    }
}

@Composable
private fun SetupNavHost(navController: NavHostController) {
    // From API level 31 on, there is a default Splashscreen.
    val startDestination = if (Build.VERSION.SDK_INT >= 31) {
        NavigationDestination.Home
    } else {
        NavigationDestination.Splash
    }
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = NavigationAnimation.defaultEnter,
        exitTransition = NavigationAnimation.defaultExit,
        popEnterTransition = NavigationAnimation.defaultPopEnter,
        popExitTransition = NavigationAnimation.defaultPopExit
    ) {
        splash()
        home()
        startMultiplayer()
        settings()
        game()
        composable(NavigationDestination.Statistics) { StatisticsScreen() }
        composable(NavigationDestination.History) { HistoryScreenEntry() }
        historyDetails()
        composable(NavigationDestination.Table) { TableScreen() }
    }
}

private fun NavGraphBuilder.splash() {
    composable(NavigationDestination.Splash,
        exitTransition = { fadeOut(tween(800)) }
    ) {
        AnimatedSplashScreen()
    }
}

private fun NavGraphBuilder.home() {
    composable(NavigationDestination.Home,
        enterTransition = {
            when(initialState.destination.route) {
                NavigationDestination.Splash -> {
                    fadeIn(tween(1000))
                }
                NavigationDestination.StartMultiplayer -> {
                    fadeIn(tween(400))
                }
                else -> {
                    fadeIn(tween(NavigationAnimation.DEFAULT_DURATION))
                }
            }
        },
        exitTransition = {
            if (targetState.destination.route == NavigationDestination.StartMultiplayer) {
                scaleOut(targetScale = 0.9f, animationSpec = tween(1000))
            } else {
                NavigationAnimation.defaultExit(this)
            }
        }
    ) {
        HomeScreen()
    }
}

private fun NavGraphBuilder.startMultiplayer() {
    composable(NavigationDestination.StartMultiplayer,
        enterTransition = {
            slideInVertically(
                animationSpec = tween(500),
                initialOffsetY = { it },
            )
        },
        exitTransition = {
            slideOutVertically(
                animationSpec = tween(500),
                targetOffsetY = { it },
            )
        }
    ) {
        StartMultiplayerDialogViewModelEntryPoint(hiltViewModel())
    }
}

private fun NavGraphBuilder.settings() {
    composable(NavigationDestination.Settings) {
        SettingsScreen()
    }
}

private fun NavGraphBuilder.game() {
    composable(NavigationDestination.Game,
        exitTransition = {
            if (initialState.destination.route == NavigationDestination.Home) {
                return@composable null
            } else {
                fadeOut(animationSpec = tween(NavigationAnimation.DEFAULT_DURATION))
            }
        },
        popEnterTransition = { fadeIn(animationSpec = tween(NavigationAnimation.DEFAULT_DURATION)) }
    ) {
        GameScreen()
    }
}

private fun NavGraphBuilder.historyDetails() {
    composable(
        NavigationDestination.HistoryDetails
    ) { backstackEntry ->
        val legId = backstackEntry.arguments!!.getString(NavigationArgument.LegId)!!.toLong()
        val viewModel: HistoryDetailsViewModel = hiltViewModel()
        viewModel.setLegId(legId)
        HistoryDetailsScreenEntry(viewModel)
    }
}

