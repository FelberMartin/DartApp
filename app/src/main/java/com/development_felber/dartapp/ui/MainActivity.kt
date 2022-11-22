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
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.development_felber.dartapp.data.AppearanceOption
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.ui.navigation.NavigationAnimation
import com.development_felber.dartapp.ui.navigation.NavigationDirections
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.GameScreen
import com.development_felber.dartapp.ui.screens.history.HistoryScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsViewModel
import com.development_felber.dartapp.ui.screens.home.HomeScreen
import com.development_felber.dartapp.ui.screens.home.dialogs.StartMultiplayerDialogViewModelEntryPoint
import com.development_felber.dartapp.ui.screens.settings.SettingsScreen
import com.development_felber.dartapp.ui.screens.statistics.StatisticsScreen
import com.development_felber.dartapp.ui.screens.table.TableScreen
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DartApp()
        }
    }

    @Composable
    private fun DartApp() {
        val appearanceOption = settingsRepository.appearanceOptionFlow.collectAsState(AppearanceOption.Default)
        val navController = rememberAnimatedNavController()

        DartAppTheme(
            useDarkTheme = appearanceOption.value.useDarkTheme(isSystemInDarkTheme())
        ) {
            SetupNavHost(navController)
        }

        navigationManager.commands.collectAsState().value.also { oneTimeCommand ->
            oneTimeCommand.use { command -> command.navigateWith(navController) }
        }
    }

    @Composable
    private fun SetupNavHost(navController: NavHostController) {
        // From API level 31 on, there is a default Splashscreen.
        val startDestination = if (Build.VERSION.SDK_INT >= 31) {
            NavigationDirections.Home.destination
        } else {
            NavigationDirections.Splash.destination
        }
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = NavigationAnimation.defaultEnter,
            exitTransition = NavigationAnimation.defaultExit,
            popEnterTransition = NavigationAnimation.defaultPopEnter,
            popExitTransition = NavigationAnimation.defaultPopExit
        ) {
            splashAndHome(navController)
            settings()
            game()
            statisticsAndHistory()
        }
    }

    private fun NavGraphBuilder.splashAndHome(
        navController: NavHostController
    ) {
        composable(NavigationDirections.Splash.destination,
            exitTransition = { fadeOut(tween(1000)) }
        ) {
            AnimatedSplashScreen(navController)
        }
        composable(NavigationDirections.Home.destination,
            enterTransition = {
                when(initialState.destination.route) {
                    NavigationDirections.Splash.destination -> {
                        fadeIn(tween(1000))
                    }
                    NavigationDirections.StartMultiplayer.destination -> {
                        scaleIn(initialScale = 0.9f, animationSpec = tween(1000)) +
                                expandVertically(animationSpec = tween(1000), expandFrom = Alignment.Top)
                    }
                    else -> {
                        fadeIn(tween(NavigationAnimation.DEFAULT_DURATION))
                    }
                }
            },
            exitTransition = {
                if (targetState.destination.route == NavigationDirections.StartMultiplayer.destination) {
                    scaleOut(targetScale = 0.9f, animationSpec = tween(1000))
                } else {
                    NavigationAnimation.defaultExit(this)
                }
            }
        ) {
            HomeScreen(hiltViewModel(), hiltViewModel())
        }

        composable(NavigationDirections.StartMultiplayer.destination,
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(1000),
                    initialOffsetY = { it },
                )
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(1000),
                    targetOffsetY = { it },
                )
            }
        ) {
            StartMultiplayerDialogViewModelEntryPoint(hiltViewModel())
        }
    }

    private fun NavGraphBuilder.settings() {
        composable(NavigationDirections.Settings.destination) {
            SettingsScreen(hiltViewModel())
        }
    }

    private fun NavGraphBuilder.game() {
        composable(NavigationDirections.Game.destination,
            exitTransition = {
                if (initialState.destination.route == NavigationDirections.Home.destination) {
                    return@composable null
                } else {
                    fadeOut(animationSpec = tween(NavigationAnimation.DEFAULT_DURATION))
                }
            },
            popEnterTransition = { fadeIn(animationSpec = tween(NavigationAnimation.DEFAULT_DURATION)) }
        ) {
            GameScreen(hiltViewModel())
        }
    }

    private fun NavGraphBuilder.statisticsAndHistory() {
        composable(NavigationDirections.Statistics.destination) {
            StatisticsScreen(hiltViewModel())
        }

        composable(NavigationDirections.History.destination) {
            HistoryScreenEntry(hiltViewModel())
        }

        composable(
            NavigationDirections.HistoryDetails.route,
            arguments = NavigationDirections.HistoryDetails.arguments
        ) { backstackEntry ->
            val legId =
                backstackEntry.arguments!!.getLong(NavigationDirections.HistoryDetails.keyLegId)
            val viewModel: HistoryDetailsViewModel = hiltViewModel()
            viewModel.setLegId(legId)
            HistoryDetailsScreenEntry(hiltViewModel())
        }

        composable(NavigationDirections.Table.destination) {
            TableScreen(hiltViewModel())
        }
    }
}

