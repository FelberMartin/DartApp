package com.example.dartapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dartapp.ui.screens.home.HomeScreen
import com.example.dartapp.ui.screens.settings.SettingsViewModel
import com.example.dartapp.ui.theme.DartAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            DartAppTheme(
                useDarkTheme = settingsViewModel.useDarkTheme(isSystemInDarkTheme())
            ) {
                HomeScreen()
            }
        }
    }
}

