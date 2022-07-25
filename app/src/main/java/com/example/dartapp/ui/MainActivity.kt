package com.example.dartapp.ui

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
import com.example.dartapp.ui.screens.home.HomeScreen
import com.example.dartapp.ui.screens.settings.SettingsScreen
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
        navigationManager.commands.collectAsState().value.also { command ->
            command.navigateOnceWith(navController)
        }

        DartAppTheme(
            useDarkTheme = appearanceOption.value.useDarkTheme(isSystemInDarkTheme())
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationDirections.Home.destination
            ) {
                composable(NavigationDirections.Home.destination) {
                    HomeScreen(
                        hiltViewModel()
                    )
                }
                composable(NavigationDirections.Settings.destination) {
                    SettingsScreen(
                        hiltViewModel()
                    )
                }

            }
        }
    }
}

