package com.example.dartapp.ui.navigation

import com.example.dartapp.ui.navigation.command.NavigationCommand
import com.example.dartapp.ui.shared.OneTimeUsage
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {

    var commands = MutableStateFlow(OneTimeUsage(NavigationCommand.DO_NOTHING))

    fun navigate(command: NavigationCommand) {
        commands.value = OneTimeUsage(command)
    }

}