package com.development_felber.dartapp.ui.screens.home

import androidx.lifecycle.ViewModel
import com.development_felber.dartapp.game.GameSetup
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _isMultiplayer = MutableStateFlow(false)
    val isMultiplayer = _isMultiplayer.asStateFlow()


    fun setMultiplayer(isMultiplayer: Boolean) {
        _isMultiplayer.value = isMultiplayer
    }


    fun onTrainClicked() {
        if (isMultiplayer.value) {
            navigationManager.navigate(NavigationCommand.ToStartMultiplayer)
        } else {
            navigationManager.navigate(NavigationCommand.ToGame(GameSetup.Solo))
        }
    }

    fun navigateToSettings() {
        navigationManager.navigate(NavigationCommand.ToSettings)
    }

    fun navigateToStatistics() {
        navigationManager.navigate(NavigationCommand.ToStatistics)
    }

}