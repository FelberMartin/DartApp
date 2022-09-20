package com.example.dartapp.ui.navigation

import com.example.dartapp.ui.navigation.command.NavigationDirectionCommand

object NavigationDirections {

    val Home = NavigationDirectionCommand(
        destination = "home"
    )

    val Settings = NavigationDirectionCommand(
        destination = "settings"
    )

    val Game = NavigationDirectionCommand(
        destination = "game"
    )

    val Statistics = NavigationDirectionCommand(
        destination = "statistics"
    )

    val History = NavigationDirectionCommand(
        destination = "history"
    )

    val Table = NavigationDirectionCommand(
        destination = "table"
    )

}