package com.development_felber.dartapp.ui.navigation

import androidx.navigation.NavController
import com.development_felber.dartapp.game.GameSetup


object NavigationScreen {
    const val Splash = "splash"
    const val Home = "home"
    const val Settings = "settings"

    const val StartMultiplayer = "startMultiplayer"

    const val Game = "game"

    const val Statistics = "statistics"

    const val History = "history"
    const val HistoryDetails = "historyDetails"
    const val Table = "table"
}

object NavigationArgument {
    const val LegId = "legId"
}

object NavigationDestination {
    const val Splash = NavigationScreen.Splash
    const val Home = NavigationScreen.Home
    const val Settings = NavigationScreen.Settings

    const val StartMultiplayer = NavigationScreen.StartMultiplayer

    const val Game = NavigationScreen.Game

    const val Statistics = NavigationScreen.Statistics

    const val History = NavigationScreen.History
    const val HistoryDetails = "${NavigationScreen.HistoryDetails}/{${NavigationArgument.LegId}}"
    const val Table = NavigationScreen.Table
}

sealed class NavigationCommand() {

    abstract fun navigateWith(navController: NavController)

    object DoNothing : NavigationCommand() {
        override fun navigateWith(navController: NavController) {
            // NO-OP
        }
    }

    object Back : NavigationCommand() {
        override fun navigateWith(navController: NavController) {
            navController.navigateUp()
        }
    }

    open class SimpleNavigationCommand(
        val destination: String,
        val popBackStackBefore: Boolean = false
    ) : NavigationCommand() {
        override fun navigateWith(navController: NavController) {
            if (popBackStackBefore) {
                navController.popBackStack()
            }
            navController.navigate(destination)
        }
    }

    object ToHome : SimpleNavigationCommand(NavigationDestination.Home, popBackStackBefore = true)
    object ToSettings : SimpleNavigationCommand(NavigationDestination.Settings)
    object ToStartMultiplayer : SimpleNavigationCommand(NavigationDestination.StartMultiplayer)
    object ToStatistics : SimpleNavigationCommand(NavigationDestination.Statistics)
    object ToHistory : SimpleNavigationCommand(NavigationDestination.History)
    object ToTable : SimpleNavigationCommand(NavigationDestination.Table)

    class ToHistoryDetails(private val legId: Long) : NavigationCommand() {
        override fun navigateWith(navController: NavController) {
            navController.navigate("${NavigationScreen.HistoryDetails}/$legId")
        }
    }

    class ToGame(private val gameSetup: GameSetup) : SimpleNavigationCommand(
        destination = NavigationDestination.Game,
        popBackStackBefore = gameSetup is GameSetup.Multiplayer
    ) {
        override fun navigateWith(navController: NavController) {
            GameSetupHolder.gameSetup = gameSetup
            super.navigateWith(navController)
        }
    }

}