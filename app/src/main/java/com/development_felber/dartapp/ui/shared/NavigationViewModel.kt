package com.development_felber.dartapp.ui.shared

import androidx.lifecycle.ViewModel
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.navigation.command.NavigationCommand

abstract class NavigationViewModel(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun navigate(navigationCommand: NavigationCommand) {
        navigationManager.navigate(navigationCommand)
    }
}