package com.development_felber.dartapp.ui.screens.home

import com.development_felber.dartapp.ui.navigation.NavigationDirections
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    navigationManager: NavigationManager
) : NavigationViewModel(navigationManager) {

    private val _isMultiplayer = MutableStateFlow(false)
    val isMultiplayer = _isMultiplayer.asStateFlow()


    fun setMultiplayer(isMultiplayer: Boolean) {
        _isMultiplayer.value = isMultiplayer
    }


    fun onTrainClicked() {
        if (isMultiplayer.value) {
            navigate(NavigationDirections.StartMultiplayer)
        } else {
            navigate(NavigationDirections.Game)
        }
    }

}