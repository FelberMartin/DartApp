package com.example.dartapp.ui.navigation

import com.example.dartapp.ui.navigation.command.NavigationCommand
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {

    var commands = MutableStateFlow(NavigationCommand.DO_NOTHING)

    fun navigate(
        command: NavigationCommand
    ) {
        commands.value = command
    }

}