package com.development_felber.dartapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.development_felber.dartapp.data.AppearanceOption
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.ui.navigation.NavigationDirections
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.GameScreen
import com.development_felber.dartapp.ui.screens.history.HistoryScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsScreenEntry
import com.development_felber.dartapp.ui.screens.historydetails.HistoryDetailsViewModel
import com.development_felber.dartapp.ui.screens.home.HomeScreen
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
        val width = 300
        val duration = 500
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { width },
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(duration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -width },
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(duration))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -width },
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(duration))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { width },
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(duration))
            },
        ) {
            composable(NavigationDirections.Splash.destination,
                exitTransition = { fadeOut(tween(1000)) }
            ) {
                AnimatedSplashScreen(navController)
            }
            composable(NavigationDirections.Home.destination,
                enterTransition = { fadeIn(tween(1000)) }
            ) {
                HomeScreen(hiltViewModel(), hiltViewModel())
            }
            composable(NavigationDirections.Settings.destination) {
                SettingsScreen(hiltViewModel())
            }

            composable(NavigationDirections.Game.destination,
                exitTransition = {
                     if (initialState.destination.route == NavigationDirections.Home.destination) {
                         return@composable null
                     } else {
                         fadeOut(animationSpec = tween(duration))
                     }
                },
                popEnterTransition = { fadeIn(animationSpec = tween(duration)) }
            ) {
                GameScreen(hiltViewModel())
            }

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
                val legId = backstackEntry.arguments!!.getLong(NavigationDirections.HistoryDetails.keyLegId)
                val viewModel: HistoryDetailsViewModel = hiltViewModel()
                viewModel.setLegId(legId)
                HistoryDetailsScreenEntry(hiltViewModel())
            }

            composable(NavigationDirections.Table.destination) {
                TableScreen(hiltViewModel())
            }
        }
    }
}

