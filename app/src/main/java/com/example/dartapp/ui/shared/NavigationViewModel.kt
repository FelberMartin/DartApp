package com.example.dartapp.ui.shared

import androidx.lifecycle.ViewModel
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.navigation.command.NavigationCommand

abstract class NavigationViewModel(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun navigate(navigationCommand: NavigationCommand) {
        navigationManager.navigate(navigationCommand)
    }
}