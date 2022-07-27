package com.example.dartapp.ui.navigation

import androidx.navigation.NamedNavArgument
import com.example.dartapp.ui.navigation.command.NavigationDirectionCommand

object NavigationDirections {

    val Home = NavigationDirectionCommand(
        arguments = emptyList<NamedNavArgument>(),
        destination = "home"
    )

    val Settings = NavigationDirectionCommand(
        arguments = emptyList<NamedNavArgument>(),
        destination = "settings"
    )

    val Game = NavigationDirectionCommand(
        arguments = emptyList(),
        destination = "game"
    )

}