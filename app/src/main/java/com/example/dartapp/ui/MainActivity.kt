package com.example.dartapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dartapp.data.AppearanceOption
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.game.GameScreen
import com.example.dartapp.ui.screens.history.HistoryScreenEntry
import com.example.dartapp.ui.screens.historydetails.HistoryDetailsScreenEntry
import com.example.dartapp.ui.screens.historydetails.HistoryDetailsViewModel
import com.example.dartapp.ui.screens.home.HomeScreen
import com.example.dartapp.ui.screens.settings.SettingsScreen
import com.example.dartapp.ui.screens.statistics.StatisticsScreen
import com.example.dartapp.ui.screens.table.TableScreen
import com.example.dartapp.ui.theme.DartAppTheme
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
            DartApp()
        }
    }

    @Composable
    private fun DartApp() {
        val appearanceOption = settingsRepository.appearanceOptionFlow.collectAsState(AppearanceOption.Default)
        val navController = rememberNavController()
        navigationManager.commands.collectAsState().value.also { oneTimeCommand ->
            oneTimeCommand.use { command -> command.navigateWith(navController) }
        }

        DartAppTheme(
            useDarkTheme = appearanceOption.value.useDarkTheme(isSystemInDarkTheme())
        ) {
            // From API level 31 on, there is a default Splashscreen.
            val startDestination = if (Build.VERSION.SDK_INT >= 31) {
                NavigationDirections.Home.destination
            } else {
                NavigationDirections.Splash.destination
            }
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(NavigationDirections.Splash.destination) {
                    AnimatedSplashScreen(navController)
                }
                composable(NavigationDirections.Home.destination) {
                    HomeScreen(hiltViewModel(), hiltViewModel())
                }
                composable(NavigationDirections.Settings.destination) {
                    SettingsScreen(hiltViewModel())
                }

                composable(NavigationDirections.Game.destination) {
                    GameScreen(hiltViewModel())
                }

                composable(NavigationDirections.Statistics.destination) {
                    StatisticsScreen(hiltViewModel())
                }

                composable(NavigationDirections.History.destination) {
                    HistoryScreenEntry(hiltViewModel())
                }

                composable(NavigationDirections.HistoryDetails.route,
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
}

