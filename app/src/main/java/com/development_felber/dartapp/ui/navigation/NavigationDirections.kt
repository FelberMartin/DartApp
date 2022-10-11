package com.development_felber.dartapp.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.development_felber.dartapp.ui.navigation.command.NavigationDirectionCommand

object NavigationDirections {

    val Splash = NavigationDirectionCommand(
        destination = "splash"
    )

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

    object HistoryDetails {

        const val keyLegId = "legId"
        private const val plainRoute = "historyDetails"
        const val route = "$plainRoute/{$keyLegId}"
        val arguments = listOf(
            navArgument(keyLegId) { type = NavType.LongType }
        )

        fun navigationCommand(legId: Long) = NavigationDirectionCommand (
            destination = "$plainRoute/$legId",
            arguments = arguments
        )
    }

}