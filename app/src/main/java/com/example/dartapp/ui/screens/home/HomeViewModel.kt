package com.example.dartapp.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    var gameModeType by mutableStateOf(GameMode.Type.X01)
        private set


    fun changeGameModeType(newGameModeType: GameMode.Type) {
        viewModelScope.launch {
            // TODO: how to store?
            gameModeType = newGameModeType
        }
    }

    fun onPlayPressed() {
        // TODO: Navigate to new Game with gameModeType
    }

    fun onSettingsPressed() {
        navigationManager.navigate(NavigationDirections.Settings)
    }

}