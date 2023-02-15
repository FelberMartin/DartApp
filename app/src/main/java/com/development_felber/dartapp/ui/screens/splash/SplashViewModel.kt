package com.development_felber.dartapp.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
) : ViewModel() {

    fun onSplashFinished() {
        navigationManager.navigate(NavigationCommand.ToHome)
    }
}