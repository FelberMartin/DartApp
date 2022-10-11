package com.development_felber.dartapp.ui.navigation

import com.development_felber.dartapp.ui.navigation.command.NavigationCommand
import com.development_felber.dartapp.ui.shared.OneTimeUsage
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {

    var commands = MutableStateFlow(OneTimeUsage(NavigationCommand.DO_NOTHING))

    fun navigate(command: NavigationCommand) {
        commands.value = OneTimeUsage(command)
    }

}