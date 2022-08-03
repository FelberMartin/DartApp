package com.example.dartapp.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun onPlayPressed() {
        navigationManager.navigate(NavigationDirections.Game)
    }

    fun onSettingsPressed() {
        navigationManager.navigate(NavigationDirections.Settings)
    }

}