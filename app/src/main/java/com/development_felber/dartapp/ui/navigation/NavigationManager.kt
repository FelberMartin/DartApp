package com.development_felber.dartapp.ui.navigation

import com.development_felber.dartapp.ui.shared.OneTimeUsage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager {

    private val _commands = MutableStateFlow(OneTimeUsage<NavigationCommand>(NavigationCommand.DoNothing))
    val commands = _commands.asStateFlow()

    fun navigate(command: NavigationCommand) {
        _commands.value = OneTimeUsage(command)
    }
}